package timofeyqa.kafka_log.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import timofeyqa.kafka_log.data.LogEntity;

import java.util.UUID;

public interface KafkaLogsRepository extends JpaRepository<LogEntity, UUID> {
}
