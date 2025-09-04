package timofeyqa.rococo.service;

import timofeyqa.rococo.ex.NotFoundException;
import jakarta.annotation.Nonnull;
import timofeyqa.rococo.data.UserEntity;
import timofeyqa.rococo.mapper.UserMapper;
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

@Component
public class UserService {

  private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Autowired
  public UserService(UserRepository userRepository, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
  }

  @Transactional
  @KafkaListener(topics = "users", groupId = "userdata")
  public void listener(@Payload UserJson user, ConsumerRecord<String, UserJson> cr) {
    userRepository.findByUsername(user.username())
        .ifPresentOrElse(
            u -> LOG.info("### User already exist in DB, kafka event will be skipped: {}", cr.toString()),
            () -> {
              LOG.info("### Kafka consumer record: {}", cr.toString());

              UserEntity userDataEntity = new UserEntity()
                  .setUsername(user.username());

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
        return userRepository.findByUsername(username)
            .map(userMapper::fromEntity)
            .orElseThrow(() -> new NotFoundException("User with provided username: " + username + " not found"));
    }

    @Transactional @Nonnull
    public UserJson patchUser(@Nonnull UserJson patchRequest, @Nonnull String username) {
      UserEntity user = userRepository.findByUsername(username)
          .orElseThrow(() -> new NotFoundException("User with provided username: " + username + " not found"));

      if (!patchRequest.username().equals(user.getUsername())) {
        throw new IllegalArgumentException("Username can't be updated");
      }

      if (!patchRequest.id().equals(user.getId())) {
        throw new IllegalArgumentException("Id can't be updated");
      }

      if (patchRequest.firstname() != null && !patchRequest.firstname().isBlank()) {
        user.setFirstname(patchRequest.firstname());
      }

      if (patchRequest.lastname() != null && !patchRequest.lastname().isBlank()) {
        user.setLastname(patchRequest.lastname());
      }

      if (patchRequest.avatar() != null && !patchRequest.avatar().isEmpty()) {
        user.setAvatar(userMapper.toByte(patchRequest.avatar()));
      }

      return userMapper.fromEntity(userRepository.save(user));
    }
}
