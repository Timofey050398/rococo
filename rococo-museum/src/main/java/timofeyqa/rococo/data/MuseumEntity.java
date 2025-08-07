package timofeyqa.rococo.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "museum")
@Data
public class MuseumEntity {
  @Id
  @UuidGenerator(style = UuidGenerator.Style.TIME)
  @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, unique = true)
  private UUID id;

  @Column(name = "title", nullable = false, unique = true)
  @Size(max = 255)
  private String title;

  @Column(name = "description", length = 1000)
  @Size(max = 1000)
  private String description;

  @Column(name = "city")
  @Size(max = 255)
  private String city;

  @Lob
  @Column(name = "photo", columnDefinition = "LONGBLOB")
  private byte[] photo;

  @Column(name = "country_id", columnDefinition = "BINARY(16)", nullable = false)
  private UUID countryId;
}

