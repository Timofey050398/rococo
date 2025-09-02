package timofeyqa.rococo.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;
import timofeyqa.rococo.model.rest.UserJson;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import static timofeyqa.rococo.utils.PhotoConverter.convert;

@Getter
@Setter
@Entity
@Accessors(chain=true)
@Table(name = "\"user\"")
public class UserEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false, columnDefinition = "CHAR(36) DEFAULT (UUID())")
  private UUID id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column()
  private String firstname;

  @Column()
  private String lastname;

  @Column(name = "photo", columnDefinition = "bytea")
  private byte[] avatar;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private Instant updatedAt;

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
    Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    UserEntity that = (UserEntity) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
  }

  public static UserEntity fromJson(UserJson json) {
    return new UserEntity()
        .setId(json.id())
        .setUsername(json.username())
        .setFirstname(json.firstname())
        .setLastname(json.lastname())
        .setAvatar(convert(json.avatar()));
  }
}