package timofeyqa.rococo.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "painting")
@Data
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

  @JoinColumn(name = "artist_id", nullable = false)
  @ManyToOne(fetch = FetchType.EAGER)
  private ArtistEntity artist;

  @JoinColumn(name = "museum_id")
  @ManyToOne(fetch = FetchType.EAGER)
  private MuseumEntity museum;

  @Lob
  @Column(name = "content", columnDefinition = "LONGBLOB")
  private byte[] content;
}
