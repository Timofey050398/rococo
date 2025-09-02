package timofeyqa.rococo.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "painting")
@Data
@Accessors(chain=true)
public class PaintingEntity {

  @Id
  @UuidGenerator(style = UuidGenerator.Style.TIME)
  @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, unique = true)
  private UUID id;

  @Column(name = "title", nullable = false)
  @Size(min = 1, max = 100)
  private String title;

  @Column(name = "description", length = 1000)
  @Size(max = 1000)
  private String description;

  @Column(name = "artist_id", columnDefinition = "BINARY(16)", nullable = false)
  private UUID artistId;

  @Column(name = "museum_id", columnDefinition = "BINARY(16)")
  private UUID museumId;

  @Lob
  @Column(name = "content", columnDefinition = "LONGBLOB")
  private byte[] content;
}
