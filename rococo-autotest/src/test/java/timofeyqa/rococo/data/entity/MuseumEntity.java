package timofeyqa.rococo.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "museum")
@Data
@ToString
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

  @JoinColumn(name = "country_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private CountryEntity country;

  @OneToMany(mappedBy = "museum", fetch = FetchType.LAZY)
  @ToString.Exclude
  private Set<PaintingEntity> paintings = new HashSet<>();
}

