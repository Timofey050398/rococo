package timofeyqa.rococo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import timofeyqa.rococo.data.repository.UserRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import timofeyqa.rococo.data.UserEntity;
import timofeyqa.rococo.ex.NotFoundException;
import timofeyqa.rococo.model.UserJson;
import timofeyqa.rococo.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RococoUserdataConsumerTest {

    @Mock
    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository);
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
        userService.listener(user, record);

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
        userService.listener(user, record);

        // then
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());

        UserEntity savedEntity = captor.getValue();
        assertEquals("new_user", savedEntity.getUsername());
    }

    @Test
    void getUser_existingUser_returnsUserJson() {
        String username = "john";
        UserEntity entity = new UserEntity();
        entity.setId(UUID.randomUUID());
        entity.setUsername(username);
        entity.setFirstname("John");
        entity.setLastname("Doe");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(entity));

        UserJson result = userService.getUser(username);

        assertEquals(username, result.username());
        assertEquals("John", result.firstname());
        assertEquals("Doe", result.lastname());
    }

    @Test
    void getUser_nonExistingUser_throwsNotFound() {
        String username = "unknown";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
            () -> userService.getUser(username));

        assertTrue(ex.getMessage().contains(username));
    }

    @Test
    void patchUser_updatesAllowedFields() {
        String username = "john";
        UUID id = UUID.randomUUID();

        UserEntity entity = new UserEntity();
        entity.setId(id);
        entity.setUsername(username);
        entity.setFirstname("OldFirst");
        entity.setLastname("OldLast");
        entity.setAvatar("old".getBytes(StandardCharsets.UTF_8));

        UserJson patchRequest = new UserJson(id, username, "NewFirst", "NewLast", "newAvatar");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(entity));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        UserJson result = userService.patchUser(patchRequest, username);

        assertEquals("NewFirst", result.firstname());
        assertEquals("NewLast", result.lastname());
        assertArrayEquals("newAvatar".getBytes(StandardCharsets.UTF_8), result.avatar().getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void patchUser_usernameMismatch_throwsException() {
        String username = "john";
        UserEntity entity = new UserEntity();
        entity.setId(UUID.randomUUID());
        entity.setUsername(username);

        UserJson patchRequest = new UserJson(entity.getId(), "otherUsername", "First", "Last", null);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(entity));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> userService.patchUser(patchRequest, username));

        assertTrue(ex.getMessage().contains("Username can't be updated"));
    }

    @Test
    void patchUser_idMismatch_throwsException() {
        String username = "john";
        UserEntity entity = new UserEntity();
        entity.setId(UUID.randomUUID());
        entity.setUsername(username);

        UserJson patchRequest = new UserJson(UUID.randomUUID(), username, "First", "Last", null);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(entity));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> userService.patchUser(patchRequest, username));

        assertTrue(ex.getMessage().contains("Id can't be updated"));
    }
}
