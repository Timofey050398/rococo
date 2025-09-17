package timofeyqa.rococo.logging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.joran.spi.JoranException;
import com.github.danielwegener.logback.kafka.KafkaAppender;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.kafka.test.EmbeddedKafkaBroker;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GeoKafkaLoggingIntegrationTest {

  private static final String LOG_TOPIC = "logs";

  private EmbeddedKafkaBroker embeddedKafka;
  private LoggerContext loggerContext;

  @BeforeAll
  void setUp() throws Exception {
    embeddedKafka = new EmbeddedKafkaBroker(1, true, 1, LOG_TOPIC);
    embeddedKafka.afterPropertiesSet();

    Path logsDir = Path.of("build", "test-logs", "rococo-geo");
    Files.createDirectories(logsDir);

    loggerContext = new LoggerContext();
    loggerContext.setName("geo-logging-test");
    loggerContext.putProperty("spring.kafka.bootstrap-servers", embeddedKafka.getBrokersAsString());
    loggerContext.putProperty("spring.application.name", "rococo-geo");
    loggerContext.putProperty("LOG_PATH", logsDir.toAbsolutePath().toString());

    configureLogback();
  }

  @AfterAll
  void tearDown() {
    if (loggerContext != null) {
      loggerContext.stop();
    }
    if (embeddedKafka != null) {
      embeddedKafka.destroy();
    }
  }

  @Test
  void shouldSendGeoLogsToKafkaTopic() {
    Appender<ILoggingEvent> kafkaAppender = loggerContext
        .getLogger(Logger.ROOT_LOGGER_NAME)
        .getAppender("KAFKA");

    assertNotNull(kafkaAppender, "Kafka appender must be configured in logback-spring.xml");
    assertTrue(kafkaAppender instanceof KafkaAppender,
        "Root logger should contain KafkaAppender instance");

    String marker = UUID.randomUUID().toString();
    Logger logger = loggerContext.getLogger("geo.kafka.test");

    try (KafkaConsumer<String, String> consumer = createConsumer()) {
      consumer.subscribe(Collections.singletonList(LOG_TOPIC));
      consumer.poll(Duration.ZERO);

      logger.info("Geo service log marker {}", marker);

      String payload = awaitPayload(consumer, marker);

      assertNotNull(payload, "Expected log entry to be delivered to Kafka topic");
      assertTrue(payload.contains(marker), "Kafka payload must contain logged marker");
      assertTrue(payload.contains("\"service\":\"rococo-geo\""),
          "Kafka payload must contain service name from logback configuration");
    }
  }

  private void configureLogback() throws JoranException, IOException {
    JoranConfigurator configurator = new JoranConfigurator();
    configurator.setContext(loggerContext);
    try (InputStream config = getClass().getClassLoader().getResourceAsStream("logback-spring.xml")) {
      if (config == null) {
        throw new IllegalStateException("logback-spring.xml not found in classpath");
      }
      configurator.doConfigure(config);
    }
  }

  private KafkaConsumer<String, String> createConsumer() {
    Properties properties = new Properties();
    properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafka.getBrokersAsString());
    properties.put(ConsumerConfig.GROUP_ID_CONFIG, "geo-logging-test-" + UUID.randomUUID());
    properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
    return new KafkaConsumer<>(properties);
  }

  private String awaitPayload(KafkaConsumer<String, String> consumer, String marker) {
    long end = System.currentTimeMillis() + Duration.ofSeconds(5).toMillis();
    while (System.currentTimeMillis() < end) {
      ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(200));
      for (ConsumerRecord<String, String> record : records) {
        String value = record.value();
        if (value != null && value.contains(marker)) {
          return value;
        }
      }
    }
    return null;
  }
}
