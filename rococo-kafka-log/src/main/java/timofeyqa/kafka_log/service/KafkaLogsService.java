package timofeyqa.kafka_log.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timofeyqa.kafka_log.data.repository.KafkaLogsRepository;
import timofeyqa.kafka_log.mapper.LogMapper;
import timofeyqa.kafka_log.model.LogJson;

@Service
@Slf4j
public class KafkaLogsService {

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
  public void listener(@Payload String payload, ConsumerRecord<String, String> cr) {
    try {
      LogJson logJson = objectMapper.readValue(payload, LogJson.class);
      kafkaLogsRepository.save(logMapper.toEntity(logJson));
    } catch (JsonProcessingException e) {
      log.error("### Can't parse josn: {}", e);
      throw new RuntimeException(e);
    }
  }
}
