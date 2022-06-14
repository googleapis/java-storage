package com.google.cloud.storage;

import static com.google.common.truth.Truth.assertThat;

import com.google.api.core.ApiFuture;
import com.google.api.gax.paging.AbstractPage;
import com.google.api.gax.rpc.ApiCallContext;
import com.google.api.gax.rpc.PageContext;
import com.google.api.gax.rpc.PagedListDescriptor;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.storage.GrpcStorageImpl.TransformingPageDecorator;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class GrpcTransformPageDecoratorTest {

  @Test
  public void valueTranslationTest() {
    List<String> initialValues = Arrays.asList("string1", "string2", "string3");
    List<String> expectedValues = Arrays.asList("STRING1", "STRING2", "STRING3");
    StringPagedListDescriptor descriptor = new StringPagedListDescriptor();
    UnaryCallable callable = new StringPagedCallable();
    PageContext<String, List<String>, String> context = new StringPageContext(descriptor, callable);
    ListStringPage page = new ListStringPage(context, initialValues);
    TransformingPageDecorator decorator =
        new TransformingPageDecorator<>(page, String::toUpperCase);
    assertThat(ImmutableList.copyOf(decorator.getValues().iterator()))
        .containsExactlyElementsIn(expectedValues);
    assertThat(ImmutableList.copyOf(decorator.iterateAll().iterator()))
        .containsExactlyElementsIn(expectedValues);
  }

  private class ListStringPage extends AbstractPage<String, List<String>, String, ListStringPage> {

    protected ListStringPage(
        PageContext<String, List<String>, String> context, List<String> response) {
      super(context, response);
    }

    @Override
    protected ListStringPage createPage(
        PageContext<String, List<String>, String> context, List<String> response) {
      return new ListStringPage(context, response);
    }
  }

  private static class StringPagedListDescriptor
      implements PagedListDescriptor<String, List<String>, String> {
    @Override
    public String emptyToken() {
      return "";
    }

    @Override
    public String injectToken(String payload, String token) {
      return null;
    }

    @Override
    public String injectPageSize(String payload, int pageSize) {
      return null;
    }

    @Override
    public Integer extractPageSize(String payload) {
      return null;
    }

    @Override
    public String extractNextToken(List<String> payload) {
      return emptyToken();
    }

    @Override
    public Iterable<String> extractResources(List<String> payload) {
      return payload;
    }
  }

  private class StringPageContext extends PageContext<String, List<String>, String> {
    private PagedListDescriptor pageDescriptor;
    private UnaryCallable callable;

    private StringPageContext(PagedListDescriptor pageDescriptor, UnaryCallable callable) {
      this.pageDescriptor = pageDescriptor;
      this.callable = callable;
    }

    @Override
    public UnaryCallable<String, List<String>> getCallable() {
      return callable;
    }

    @Override
    public PagedListDescriptor<String, List<String>, String> getPageDescriptor() {
      return pageDescriptor;
    }

    @Override
    public String getRequest() {
      return null;
    }

    @Override
    public ApiCallContext getCallContext() {
      return null;
    }
  }

  private class StringPagedCallable extends UnaryCallable<String, List<String>> {

    @Override
    public ApiFuture<List<String>> futureCall(String request, ApiCallContext context) {
      return null;
    }
  }
}
