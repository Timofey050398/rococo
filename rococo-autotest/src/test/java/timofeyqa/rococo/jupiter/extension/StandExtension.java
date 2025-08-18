package timofeyqa.rococo.jupiter.extension;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.SearchOption;
import timofeyqa.rococo.config.Config;
import timofeyqa.rococo.config.Profile;
import timofeyqa.rococo.jupiter.annotation.OnProfile;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;


public class StandExtension implements ExecutionCondition {

  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
    Class<?> clazz = context.getRequiredTestClass();
    Optional<Method> method = context.getTestMethod();

    Optional<OnProfile> annotation = method
        .flatMap(m -> AnnotationSupport.findAnnotation(m, OnProfile.class));

    if (annotation.isEmpty()) {
      annotation = AnnotationSupport.findAnnotation(
          clazz,
          OnProfile.class,
          SearchOption.INCLUDE_ENCLOSING_CLASSES
      );
    }

    return annotation
        .map(profile -> {
          boolean matches = Arrays.stream(profile.value())
              .map(Profile::getInstance)
              .anyMatch(cfg -> cfg.equals(Config.getInstance()));

          return matches
              ? ConditionEvaluationResult.enabled("Enabled on this stand: " + Config.getInstance())
              : ConditionEvaluationResult.disabled("Disabled on this stand: " + Config.getInstance());
        })
        .orElseGet(() ->
            ConditionEvaluationResult.enabled("Annotation @OnProfile not found")
        );
  }

}
