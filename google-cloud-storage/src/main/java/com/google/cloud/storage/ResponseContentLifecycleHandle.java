package com.google.cloud.storage;

import com.google.storage.v2.ReadObjectResponse;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

final class ResponseContentLifecycleHandle implements Closeable {
    @Nullable
    private final Closeable dispose;

    private final List<ByteBuffer> buffers;

    ResponseContentLifecycleHandle(ReadObjectResponse resp, @Nullable Closeable dispose) {
        this.dispose = dispose;

        this.buffers = resp.getChecksummedData().getContent().asReadOnlyByteBufferList();
    }

    void copy(ReadCursor c, ByteBuffer[] dsts, int offset, int length) {
        for (ByteBuffer b : buffers) {
            long copiedBytes = Buffers.copy(b, dsts, offset, length);
            c.advance(copiedBytes);
            if (b.hasRemaining()) break;
        }
    }

    boolean hasRemaining() {
        for (ByteBuffer b : buffers) {
            if (b.hasRemaining()) return true;
        }
        return false;
    }

    @Override
    public void close() throws IOException {
        if (dispose != null) {
            dispose.close();
        }
    }
}
