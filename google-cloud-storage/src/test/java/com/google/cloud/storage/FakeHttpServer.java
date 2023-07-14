/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.storage;

import static io.grpc.netty.shaded.io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.grpc.netty.shaded.io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.grpc.netty.shaded.io.netty.handler.codec.http.HttpHeaderValues.CLOSE;

import io.grpc.netty.shaded.io.netty.bootstrap.ServerBootstrap;
import io.grpc.netty.shaded.io.netty.buffer.ByteBuf;
import io.grpc.netty.shaded.io.netty.channel.Channel;
import io.grpc.netty.shaded.io.netty.channel.ChannelFuture;
import io.grpc.netty.shaded.io.netty.channel.ChannelFutureListener;
import io.grpc.netty.shaded.io.netty.channel.ChannelHandlerContext;
import io.grpc.netty.shaded.io.netty.channel.ChannelInitializer;
import io.grpc.netty.shaded.io.netty.channel.ChannelOption;
import io.grpc.netty.shaded.io.netty.channel.ChannelPipeline;
import io.grpc.netty.shaded.io.netty.channel.EventLoopGroup;
import io.grpc.netty.shaded.io.netty.channel.SimpleChannelInboundHandler;
import io.grpc.netty.shaded.io.netty.channel.nio.NioEventLoopGroup;
import io.grpc.netty.shaded.io.netty.channel.socket.SocketChannel;
import io.grpc.netty.shaded.io.netty.channel.socket.nio.NioServerSocketChannel;
import io.grpc.netty.shaded.io.netty.handler.codec.http.FullHttpResponse;
import io.grpc.netty.shaded.io.netty.handler.codec.http.HttpHeaders;
import io.grpc.netty.shaded.io.netty.handler.codec.http.HttpObjectAggregator;
import io.grpc.netty.shaded.io.netty.handler.codec.http.HttpRequest;
import io.grpc.netty.shaded.io.netty.handler.codec.http.HttpServerCodec;
import io.grpc.netty.shaded.io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.grpc.netty.shaded.io.netty.handler.logging.LogLevel;
import io.grpc.netty.shaded.io.netty.handler.logging.LoggingHandler;
import java.net.InetSocketAddress;
import java.net.URI;

final class FakeHttpServer implements AutoCloseable {

  private final URI endpoint;
  private final Channel channel;
  private final Runnable shutdown;

  private FakeHttpServer(URI endpoint, Channel channel, Runnable shutdown) {
    this.endpoint = endpoint;
    this.channel = channel;
    this.shutdown = shutdown;
  }

  public URI getEndpoint() {
    return endpoint;
  }

  @Override
  public void close() throws Exception {
    shutdown.run();
    channel.closeFuture().syncUninterruptibly();
  }

  static FakeHttpServer of(HttpRequestHandler server) {
    // based on
    // https://github.com/netty/netty/blob/59aa6e635b9996cf21cd946e64353270679adc73/example/src/main/java/io/netty/example/http/helloworld/HttpHelloWorldServer.java
    InetSocketAddress address = new InetSocketAddress("localhost", 0);
    // Configure the server.
    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    ServerBootstrap b = new ServerBootstrap();
    b.option(ChannelOption.SO_BACKLOG, 1024);
    b.group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        .handler(new LoggingHandler(LogLevel.DEBUG))
        .childHandler(
            new ChannelInitializer<SocketChannel>() {
              @Override
              protected void initChannel(SocketChannel ch) {
                ChannelPipeline p = ch.pipeline();
                p.addLast(new HttpServerCodec());
                // Accept a request and content up to 100 MiB
                // If we don't do this, sometimes the ordering on the wire will result in the server
                // rejecting the request before the client has finished sending.
                // While our client can handle this scenario and retry, it makes assertions more
                // difficult due to the variability of request counts.
                p.addLast(new HttpObjectAggregator(100 * 1024 * 1024));
                p.addLast(new HttpServerExpectContinueHandler());
                p.addLast(new Handler(server));
              }
            });

    Channel channel = b.bind(address).syncUninterruptibly().channel();

    InetSocketAddress socketAddress = (InetSocketAddress) channel.localAddress();
    return new FakeHttpServer(
        URI.create("http://localhost:" + socketAddress.getPort()),
        channel,
        () -> {
          bossGroup.shutdownGracefully();
          workerGroup.shutdownGracefully();
        });
  }

  interface HttpRequestHandler {
    FullHttpResponse apply(HttpRequest req) throws Exception;
  }

  /**
   * Based on
   * https://github.com/netty/netty/blob/59aa6e635b9996cf21cd946e64353270679adc73/example/src/main/java/io/netty/example/http/helloworld/HttpHelloWorldServerHandler.java
   */
  private static final class Handler extends SimpleChannelInboundHandler<HttpRequest> {

    private final HttpRequestHandler server;

    private Handler(HttpRequestHandler server) {
      this.server = server;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
      ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest req) throws Exception {
      FullHttpResponse resp = server.apply(req);
      HttpHeaders headers = resp.headers();
      if (!headers.contains(CONTENT_LENGTH)) {
        ByteBuf content = resp.content();
        headers.setInt(CONTENT_LENGTH, content.readableBytes());
      }
      headers.set(CONNECTION, CLOSE);
      ChannelFuture future = ctx.writeAndFlush(resp);
      future.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
      cause.printStackTrace();
      ctx.close();
    }
  }
}
