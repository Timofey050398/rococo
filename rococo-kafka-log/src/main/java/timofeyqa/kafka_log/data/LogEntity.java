package timofeyqa.kafka_log.data;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Table(name = "logs")
@Data
@Entity
public class LogEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false, columnDefinition = "CHAR(36) DEFAULT (UUID())")
  private UUID id;

  @Column(nullable = false)
  private Service service;

  @Column(nullable = false)
  private String level;

  @Column
  private String message;

  @Column
  private String thread;

  @Column
  private String logger;

  @Column
  private Instant timestamp;

}
