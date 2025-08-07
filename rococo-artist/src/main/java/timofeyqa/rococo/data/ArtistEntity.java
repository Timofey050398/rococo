package timofeyqa.rococo.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "artist")
@Data
public class ArtistEntity {
  @Id
  @UuidGenerator(style = UuidGenerator.Style.TIME)
  @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, unique = true)
  private UUID id;

  @Column(name = "name", nullable = false, unique = true)
  @Size(max = 255)
  private String name;

  @Column(name = "biography", nullable = false, length = 2000)
  @Size(max = 2000)
  private String biography;

  @Lob
  @Column(name = "photo", columnDefinition = "LONGBLOB")
  private byte[] photo;
}
