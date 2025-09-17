package timofeyqa.kafka_log.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timofeyqa.kafka_log.data.repository.KafkaLogsRepository;
import timofeyqa.kafka_log.mapper.LogMapper;
import timofeyqa.kafka_log.model.LogJson;

@Service
public class KafkaLogsService {

  private static final Logger log = LoggerFactory.getLogger(KafkaLogsService.class);

  private final KafkaLogsRepository kafkaLogsRepository;
  private final LogMapper logMapper;
  private final ObjectMapper objectMapper;

  @Autowired
  public KafkaLogsService(KafkaLogsRepository kafkaLogsRepository, LogMapper logMapper, ObjectMapper objectMapper) {
    this.kafkaLogsRepository = kafkaLogsRepository;
    this.logMapper = logMapper;
    this.objectMapper = objectMapper;
  }

  @Transactional
  @KafkaListener(topics = "logs", groupId = "logs")
  public void listener(@Payload byte[] payload) {
    try {
      LogJson logJson = objectMapper.readValue(payload, LogJson.class);
      kafkaLogsRepository.save(logMapper.toEntity(logJson));
    } catch (IOException e) {
      log.error("Failed to deserialize log payload", e);
      throw new IllegalArgumentException("Failed to deserialize log payload", e);
    }
  }
}
