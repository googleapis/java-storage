package com.google.cloud.storage;

import static com.google.common.truth.Truth.assertThat;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.paging.AbstractPage;
import com.google.api.gax.rpc.ApiCallContext;
import com.google.api.gax.rpc.PageContext;
import com.google.api.gax.rpc.PagedListDescriptor;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.storage.GrpcStorageImpl.TransformingPageDecorator;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;
import org.mockito.Mockito;

public class GrpcTransformPageDecoratorTest {

  @Test
  public void valueTranslationTest() {
    // Initial values for the first page
    List<String> initialValues = Arrays.asList("string1", "string2", "string3");
    // Request which will be appended to the second page
    String request = "string4";

    // Expected values after the translation
    List<String> expectedValuesPageOne = Arrays.asList("STRING1", "STRING2", "STRING3");
    List<String> expectedValuesPageTwo = Arrays.asList("STRING4");
    List<String> expectedPagesValuesMerged =
        Stream.of(expectedValuesPageOne, expectedValuesPageTwo)
            .flatMap(list -> list.stream())
            .collect(Collectors.toList());

    StringPagedListDescriptor descriptor = new StringPagedListDescriptor();
    UnaryCallable callable = new StringPagedCallable();
    PageContext<String, List<String>, String> context =
        PageContext.create(callable, descriptor, request, Mockito.mock(ApiCallContext.class));
    ListStringPage page = new ListStringPage(context, initialValues);
    TransformingPageDecorator decorator =
        new TransformingPageDecorator<>(page, String::toUpperCase);

    assertThat(ImmutableList.copyOf(decorator.getValues().iterator()))
        .containsExactlyElementsIn(expectedValuesPageOne);
    assertThat(ImmutableList.copyOf(decorator.iterateAll().iterator()))
        .containsExactlyElementsIn(expectedPagesValuesMerged);
  }

  private class ListStringPage extends AbstractPage<String, List<String>, String, ListStringPage> {

    public ListStringPage(
        PageContext<String, List<String>, String> context, List<String> response) {
      super(context, response);
    }

    @Override
    public ListStringPage createPage(
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
      return payload;
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
      return payload.size() > 0 ? payload.get(0) : emptyToken();
    }

    @Override
    public Iterable<String> extractResources(List<String> payload) {
      return payload;
    }
  }

  private class StringPagedCallable extends UnaryCallable<String, List<String>> {
    // We only want to add one additional page with the same value as the request,
    // this is kind of hacky, but I wanted to validate we are performing iterate all
    // properly.
    private int numberOfPages = 1;

    @Override
    public ApiFuture<List<String>> futureCall(String request, ApiCallContext context) {
      if (numberOfPages-- > 0) {
        return ApiFutures.immediateFuture(Arrays.asList(request));
      }
      return ApiFutures.immediateFuture(Collections.emptyList());
    }
  }
}
