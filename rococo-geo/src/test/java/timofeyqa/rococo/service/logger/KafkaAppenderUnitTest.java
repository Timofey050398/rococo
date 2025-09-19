package timofeyqa.rococo.service.logger;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.github.danielwegener.logback.kafka.KafkaAppender;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Юнит-тест проверяет, что logback KafkaAppender реально шлёт логи в KafkaProducer.
 * Сам Kafka не поднимаем — мокаем продюсер и проверяем вызов send().
 */
class KafkaAppenderUnitTest {

  @Test
  void shouldSendLogThroughKafkaAppender() throws Exception {
    // достаём root-логгер
    Logger rootLogger = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

    // достаём аппендер "KAFKA" из logback-test.xml
    Appender<ILoggingEvent> appender = rootLogger.getAppender("KAFKA");
    assertThat(appender).isNotNull();
    assertThat(appender).isInstanceOf(KafkaAppender.class);

    KafkaAppender<?> kafkaAppender = (KafkaAppender<?>) appender;

    // создаём мок продюсера
    @SuppressWarnings("unchecked")
    Producer<byte[], byte[]> mockProducer = mock(Producer.class);

    // через reflection подменяем lazyProducer.producer
    Field lazyProducerField = KafkaAppender.class.getDeclaredField("lazyProducer");
    lazyProducerField.setAccessible(true);
    Object lazyProducer = lazyProducerField.get(kafkaAppender);

    Field producerField = lazyProducer.getClass().getDeclaredField("producer");
    producerField.setAccessible(true);
    producerField.set(lazyProducer, mockProducer);

    // Логируем сообщение
    org.slf4j.Logger log = LoggerFactory.getLogger(KafkaAppenderUnitTest.class);
    log.info("test-log-message");

    // Проверяем, что продюсер вызвал send() с правильными данными
    @SuppressWarnings("unchecked")
    ArgumentCaptor<ProducerRecord<byte[], byte[]>> captor =
        ArgumentCaptor.forClass(ProducerRecord.class);

    verify(mockProducer, atLeastOnce()).send(captor.capture(), any());

    ProducerRecord<byte[], byte[]> record = captor.getValue();
    assertThat(record.topic()).isEqualTo("logs");

    String value = new String(record.value(), StandardCharsets.UTF_8);
    assertThat(value).contains("test-log-message");
  }
}