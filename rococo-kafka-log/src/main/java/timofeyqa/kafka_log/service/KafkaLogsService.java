package timofeyqa.kafka_log.service;

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

  @Autowired
  public KafkaLogsService(KafkaLogsRepository kafkaLogsRepository, LogMapper logMapper) {
    this.kafkaLogsRepository = kafkaLogsRepository;
    this.logMapper = logMapper;
  }

  @Transactional
  @KafkaListener(topics = "logs", groupId = "logs")
  public void listener(@Payload LogJson logJson, ConsumerRecord<String, LogJson> cr) {
    log.info("### Kafka consumer record: {}", cr.toString());
    kafkaLogsRepository.save(logMapper.toEntity(logJson));
  }
}
