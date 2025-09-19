package timofeyqa.kafka_log.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import timofeyqa.kafka_log.data.LogEntity;
import timofeyqa.kafka_log.data.repository.KafkaLogsRepository;
import timofeyqa.kafka_log.model.LogJson;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class KafkaLogsServiceTest {

  @Autowired
  private KafkaLogsService kafkaLogsService;

  @Autowired
  private KafkaLogsRepository kafkaLogsRepository;

  private final ObjectMapper objectMapper = new ObjectMapper()
      .registerModule(new JavaTimeModule())
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

  @Test
  void logShouldBeSavedToDatabase() throws Exception {
    LogJson log = new LogJson(
        "rococo-auth",
        "INFO",
        "test-message",
        "main-thread",
        "test-logger",
        Instant.now()
    );

    String logStr = objectMapper.writeValueAsString(log);

    ConsumerRecord<String, String> cr =
        new ConsumerRecord<>("logs", 1, 1, null, logStr);

    kafkaLogsService.listener(logStr, cr);

    List<LogEntity> all = kafkaLogsRepository.findAll();
    assertThat(all).hasSize(1);
    assertThat(all.getFirst().getMessage()).isEqualTo("test-message");
    assertThat(all.getFirst().getService()).isEqualTo("rococo-auth");
    assertThat(all.getFirst().getLevel()).isEqualTo("INFO");
  }
}
