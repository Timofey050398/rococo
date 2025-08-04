package timofeyqa.rococo.service;

import timofeyqa.rococo.ex.NotFoundException;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityNotFoundException;
import timofeyqa.rococo.data.UserEntity;
import timofeyqa.rococo.model.UserJson;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import timofeyqa.rococo.data.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class UserService {

  private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional
  @KafkaListener(topics = "users", groupId = "userdata")
  public void listener(@Payload UserJson user, ConsumerRecord<String, UserJson> cr) {
    userRepository.findByUsername(user.username())
        .ifPresentOrElse(
            u -> LOG.info("### User already exist in DB, kafka event will be skipped: {}", cr.toString()),
            () -> {
              LOG.info("### Kafka consumer record: {}", cr.toString());

              UserEntity userDataEntity = new UserEntity();
              userDataEntity.setUsername(user.username());
              UserEntity userEntity = userRepository.save(userDataEntity);

              LOG.info(
                  "### User '{}' successfully saved to database with id: {}",
                  user.username(),
                  userEntity.getId()
              );
            }
        );
  }


    public @Nonnull UserJson getUser(@Nonnull String username) {
        Optional<UserEntity> entity = userRepository.findByUsername(username);

        if(entity.isPresent()) {
            return UserJson.fromEntity(entity.get());
        } else {
            throw new NotFoundException("user with provided username: " + username + " not found");
        }
    }

    @Transactional @Nonnull
    public UserJson patchUser(@Nonnull UserJson patchRequest) {
        UserEntity user = userRepository.findById(patchRequest.id())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + patchRequest.id()));

        if (!patchRequest.username().equals(user.getUsername())) {
            throw new IllegalArgumentException("Username can't be updated");
        }

        if (patchRequest.firstname() != null) {
            user.setFirstname(patchRequest.firstname());
        }

        if (patchRequest.lastname() != null) {
            user.setLastname(patchRequest.lastname());
        }

        if (patchRequest.avatar() != null && !patchRequest.avatar().isEmpty()) {
            user.setAvatar(patchRequest.avatar().getBytes(StandardCharsets.UTF_8));
        }

        return UserJson.fromEntity(userRepository.save(user));
    }
}
