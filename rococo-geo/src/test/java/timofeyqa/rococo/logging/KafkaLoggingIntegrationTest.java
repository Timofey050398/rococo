package timofeyqa.rococo.logging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.github.danielwegener.logback.kafka.KafkaAppender;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.*;
import org.springframework.kafka.test.EmbeddedKafkaKraftBroker;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KafkaLoggingIntegrationTest {

  private static final String TOPIC = "logs";

  private EmbeddedKafkaKraftBroker embeddedKafka;
  private LoggerContext loggerContext;

  @BeforeAll
  void setUp() throws Exception {
    // Запускаем embedded Kafka
    embeddedKafka = new EmbeddedKafkaKraftBroker(1, 1, TOPIC)
        .kafkaPorts(9292); // фиксируем порт
    embeddedKafka.afterPropertiesSet();

    // Настройка logback
    loggerContext = new LoggerContext();
    loggerContext.setName("test-logging");
    loggerContext.putProperty("spring.kafka.bootstrap-servers", embeddedKafka.getBrokersAsString());
    loggerContext.putProperty("spring.application.name", "rococo-geo");

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
  void logShouldBeDeliveredToKafka() throws Exception {
    // Проверяем, что аппендер настроен
    KafkaAppender<?> kafkaAppender = (KafkaAppender<?>)
        loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).getAppender("KAFKA");
    assertNotNull(kafkaAppender, "Kafka appender must exist");

    // Логируем маркер
    String marker = UUID.randomUUID().toString();

    Thread.sleep(500);

    try (KafkaConsumer<String, byte[]> consumer = createConsumer()) {
      consumer.subscribe(Collections.singletonList(TOPIC));

      String payload = awaitPayload(consumer, marker);

      assertNotNull(payload, "Expected to receive log from Kafka");
      assertTrue(payload.contains(marker), "Kafka payload should contain the marker");
      assertTrue(payload.contains("\"service\":\"rococo-geo\""),
          "Kafka payload must contain service name from logback configuration");
    }
  }

  private void configureLogback() throws JoranException {
    JoranConfigurator configurator = new JoranConfigurator();
    configurator.setContext(loggerContext);
    try (InputStream config = getClass().getClassLoader().getResourceAsStream("logback-spring.xml")) {
      if (config == null) {
        throw new IllegalStateException("logback-spring.xml not found");
      }
      configurator.doConfigure(config);
    } catch (Exception e) {
      throw new RuntimeException("Failed to configure logback", e);
    }
  }

  private KafkaConsumer<String, byte[]> createConsumer() {
    Properties props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafka.getBrokersAsString());
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-" + UUID.randomUUID());
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    return new KafkaConsumer<>(props);
  }

  private String awaitPayload(KafkaConsumer<String, byte[]> consumer, String marker) {
    long deadline = System.currentTimeMillis() + 10_000;
    while (System.currentTimeMillis() < deadline) {
      ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(200));
      for (ConsumerRecord<String, byte[]> record : records) {
        byte[] value = record.value();
        if (value != null) {
          String payload = new String(value, StandardCharsets.UTF_8);
          if (payload.contains(marker)) {
            System.out.println(">>> Received from Kafka: " + payload);
            return payload;
          }
        }
      }
    }
    return null;
  }
}
