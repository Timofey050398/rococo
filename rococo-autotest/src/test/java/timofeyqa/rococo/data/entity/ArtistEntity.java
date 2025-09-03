package timofeyqa.rococo.data.entity;

import io.qameta.allure.Param;
import io.qameta.allure.model.Parameter;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "artist")
@Data
@ToString
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

  @OneToMany(mappedBy = "artist", fetch = FetchType.LAZY)
  @ToString.Exclude
  private Set<PaintingEntity> paintings = new HashSet<>();
}
