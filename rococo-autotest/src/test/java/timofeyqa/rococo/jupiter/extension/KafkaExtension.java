package timofeyqa.rococo.jupiter.extension;

import org.junit.jupiter.api.extension.ExtensionContext;
import timofeyqa.rococo.service.kafka.KafkaService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KafkaExtension implements SuiteExtension {

  private static final KafkaService kafkaService = new KafkaService();
  private static final ExecutorService executor = Executors.newSingleThreadExecutor();

  @Override
  public void beforeSuite(ExtensionContext context) {
    executor.submit(kafkaService);
    executor.shutdown();
  }

  @Override
  public void afterSuite() {
    kafkaService.shutdown();
  }
}
