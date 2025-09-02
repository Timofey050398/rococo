package timofeyqa.rococo.jupiter.extension;

import org.junit.jupiter.api.ClassDescriptor;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.ClassOrdererContext;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.parallel.Isolated;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Comparator;

/**
 * Ensures that test classes annotated with {@link Isolated} are executed before
 * all other classes. Within each group, classes are ordered by the
 * {@link Order} annotation when present.
 */
public class IsolatedFirstClassOrderer implements ClassOrderer {

  @Override
  public void orderClasses(ClassOrdererContext context) {
    context.getClassDescriptors().sort(
        Comparator
            .comparing((ClassDescriptor cd) ->
                AnnotationSupport.isAnnotated(cd.getTestClass(), Isolated.class) ? 0 : 1)
            .thenComparing(cd ->
                AnnotationSupport.findAnnotation(cd.getTestClass(), Order.class)
                    .map(Order::value)
                    .orElse(Order.DEFAULT))
    );
  }
}
