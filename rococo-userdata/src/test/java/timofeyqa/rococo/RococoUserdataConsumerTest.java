package timofeyqa.rococo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import timofeyqa.rococo.data.repository.UserRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import timofeyqa.rococo.data.UserEntity;
import timofeyqa.rococo.model.UserJson;
import timofeyqa.rococo.service.UserService;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RococoUserdataConsumerTest {

    private UserRepository userRepository;
    private UserService consumer;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        consumer = new UserService(userRepository);
    }

    @Test
    @DisplayName("Should skip saving if user already exists")
    void shouldSkipExistingUser() {
        // given
        UserJson user = new UserJson(null,"timofey",null,null,null);
        ConsumerRecord<String, UserJson> record = new ConsumerRecord<>("users", 0, 0L, "key", user);

        when(userRepository.findByUsername("timofey"))
                .thenReturn(Optional.of(new UserEntity()));

        // when
        consumer.listener(user, record);

        // then
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should save new user")
    void shouldSaveNewUser() {
        // given
        UserJson user = new UserJson(null,"new_user",null,null,null);
        ConsumerRecord<String, UserJson> record = new ConsumerRecord<>("users", 0, 0L, "key", user);

        when(userRepository.findByUsername("new_user"))
                .thenReturn(Optional.empty());

        UserEntity saved = new UserEntity();
        saved.setId(UUID.randomUUID());
        saved.setUsername("new_user");

        when(userRepository.save(any(UserEntity.class))).thenReturn(saved);

        // when
        consumer.listener(user, record);

        // then
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());

        UserEntity savedEntity = captor.getValue();
        assertEquals("new_user", savedEntity.getUsername());
    }
}
