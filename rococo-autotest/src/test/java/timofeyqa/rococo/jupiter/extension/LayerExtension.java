package timofeyqa.rococo.jupiter.extension;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import timofeyqa.rococo.jupiter.annotation.meta.GrpcTest;
import timofeyqa.rococo.jupiter.annotation.meta.RestTest;
import timofeyqa.rococo.jupiter.annotation.meta.WebTest;

public class LayerExtension implements BeforeAllCallback {
  private static final String LAYER = "layer";

  @Override
  public void beforeAll(ExtensionContext context) {
    Class<?> testClass = context.getRequiredTestClass();
    AnnotationSupport.findAnnotation(testClass, GrpcTest.class)
        .ifPresent(grpcTest -> Allure.label(LAYER,"grpc api"));
    AnnotationSupport.findAnnotation(testClass, RestTest.class)
        .ifPresent(grpcTest -> Allure.label(LAYER,"rest api"));
    AnnotationSupport.findAnnotation(testClass, WebTest.class)
        .ifPresent(grpcTest -> Allure.label(LAYER,"web"));
  }
}
