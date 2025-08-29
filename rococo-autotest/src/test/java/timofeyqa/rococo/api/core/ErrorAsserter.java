package timofeyqa.rococo.api.core;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import retrofit2.HttpException;
import timofeyqa.rococo.model.rest.ApiError;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public interface ErrorAsserter {

  default void assertError(
      int statusCode,
      HttpException ex,
      String code,
      String message,
      String domain,
      String... errors
  ){
    assertError(
        "v1.0",
        statusCode,
        ex,
        code,
        message,
        domain,
        errors
    );
  }


  @Step("Assert error message")
  default void assertError(
      String apiVersion,
      int statusCode,
      HttpException ex,
      String code,
      String message,
      String domain,
      String... errors
  ){
    ApiError actual = ErrorParser.parseError(ex.response());
    codeAttachment(statusCode,ex.code());
    bodyAttachment(
        toAllureString(apiVersion,code,message,domain,errors),
        actual
    );

    assertEquals(statusCode,ex.code(),"code not as expected");
    assertAll(
        () -> assertEquals(apiVersion, actual.apiVersion()),
        () -> assertEquals(code, actual.code()),
        () -> assertEquals(message, actual.message()),
        () -> assertEquals(domain, actual.domain()),
        () -> {
          Set<String> actualSet = new HashSet<>(actual.errors());
          Set<String> expectedSet = Arrays.stream(errors).collect(Collectors.toSet());
          assertEquals(expectedSet, actualSet);
        });
  }

  private void codeAttachment(
      int expectedCode,
      int actualCode
  ){
    String codeAttachment = String.format("Expected code: %s\nActual code: %s", expectedCode, actualCode);
    Allure.addAttachment("status code",codeAttachment);
  }

  private void bodyAttachment(
      String expectedBody,
      ApiError actual
  ){
    String actualBody = toAllureString(actual.apiVersion(), actual.code(), actual.message(), actual.domain(), actual.errors().toArray(String[]::new));
    String attachment = String.format("Expected body: %s\nActual body: %s", expectedBody, actualBody);
    Allure.addAttachment("error body",attachment);
  }

  private String toAllureString(
      String apiVersion,
      String code,
      String message,
      String domain,
      String... errors
  ){
    return String.format(
        "apiVersion=%s, code=%s, message=%s, domain=%s, errors=%s",
        apiVersion, code, message, domain, Arrays.toString(errors)
    );
  }
}
