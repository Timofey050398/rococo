package timofeyqa.rococo.api.core;

import io.qameta.allure.attachment.AttachmentData;
import io.qameta.allure.attachment.AttachmentProcessor;
import io.qameta.allure.attachment.DefaultAttachmentProcessor;
import io.qameta.allure.attachment.FreemarkerAttachmentRenderer;
import io.qameta.allure.attachment.http.HttpRequestAttachment;
import io.qameta.allure.attachment.http.HttpResponseAttachment;
import okhttp3.*;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static timofeyqa.rococo.utils.LogUtils.maskLongParams;

public class AllureRestInterceptor implements Interceptor {

  private String requestTemplatePath = "http-request.ftl";
  private String responseTemplatePath = "http-response.ftl";

  public AllureRestInterceptor setRequestTemplate(final String templatePath) {
    this.requestTemplatePath = templatePath;
    return this;
  }

  public AllureRestInterceptor setResponseTemplate(final String templatePath) {
    this.responseTemplatePath = templatePath;
    return this;
  }

  @NotNull
  @Override
  public Response intercept(final Chain chain) throws IOException {
    final AttachmentProcessor<AttachmentData> processor = new DefaultAttachmentProcessor();

    final Request request = chain.request();
    final String requestUrl = request.url().toString();
    final HttpRequestAttachment.Builder requestAttachmentBuilder = HttpRequestAttachment.Builder
        .create("Request", requestUrl)
        .setMethod(request.method())
        .setHeaders(toMapConverter(request.headers().toMultimap()));

    final RequestBody requestBody = request.body();
    if (Objects.nonNull(requestBody) && !isAllureSendResults(chain)) {
      requestAttachmentBuilder.setBody(maskLongParams(readRequestBody(requestBody)));
    }
    final HttpRequestAttachment requestAttachment = requestAttachmentBuilder.build();
    processor.addAttachment(requestAttachment, new FreemarkerAttachmentRenderer(requestTemplatePath));

    final Response response = chain.proceed(request);
    final HttpResponseAttachment.Builder responseAttachmentBuilder = HttpResponseAttachment.Builder
        .create("Response")
        .setResponseCode(response.code())
        .setHeaders(toMapConverter(response.headers().toMultimap()));

    final Response.Builder responseBuilder = response.newBuilder();

    final ResponseBody responseBody = response.body();

    if (Objects.nonNull(responseBody) && !isAllureSendResults(chain)) {
      final byte[] bytes = responseBody.bytes();
      responseAttachmentBuilder.setBody(maskLongParams(new String(bytes, StandardCharsets.UTF_8)));
      responseBuilder.body(ResponseBody.create(responseBody.contentType(), bytes));
    }

    final HttpResponseAttachment responseAttachment = responseAttachmentBuilder.build();
    processor.addAttachment(responseAttachment, new FreemarkerAttachmentRenderer(responseTemplatePath));

    return responseBuilder.build();
  }

  private static Map<String, String> toMapConverter(final Map<String, List<String>> items) {
    final Map<String, String> result = new HashMap<>();
    items.forEach((key, value) -> result.put(key, String.join("; ", value)));
    return result;
  }

  private static String readRequestBody(final RequestBody requestBody) throws IOException {
    final Buffer buffer = new Buffer();
    requestBody.writeTo(buffer);
    return buffer.readString(StandardCharsets.UTF_8);
  }

  private boolean isAllureSendResults(Chain chain) {
    return chain.request().method().contains("allure-docker-service/send-results");
  }
}
