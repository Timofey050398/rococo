package timofeyqa.rococo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import timofeyqa.rococo.data.Authority;
import timofeyqa.rococo.data.AuthorityEntity;
import timofeyqa.rococo.data.UserEntity;
import timofeyqa.rococo.data.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class RococoUserDetailsServiceTest {

  private RococoUserDetailsService rococoUserDetailsService;
  private UserEntity testUserEntity;
  private List<AuthorityEntity> authorityEntities;

  @BeforeEach
  void initMockRepository(@Mock UserRepository userRepository) {
    AuthorityEntity read = new AuthorityEntity();
    read.setUser(testUserEntity);
    read.setAuthority(Authority.read);
    AuthorityEntity write = new AuthorityEntity();
    write.setUser(testUserEntity);
    write.setAuthority(Authority.write);
    authorityEntities = List.of(read, write);

    testUserEntity = new UserEntity()
        .setUsername("correct")
        .setAuthorities(authorityEntities)
        .setEnabled(true)
        .setPassword("test-pass")
        .setAccountNonExpired(true)
        .setAccountNonLocked(true)
        .setCredentialsNonExpired(true)
        .setId(UUID.randomUUID());

    lenient().when(userRepository.findByUsername("correct"))
        .thenReturn(Optional.of(testUserEntity));

    lenient().when(userRepository.findByUsername(not(eq("correct"))))
        .thenReturn(Optional.empty());

    rococoUserDetailsService = new RococoUserDetailsService(userRepository);
  }

  @Test
  void loadUserByUsername() {
    final UserDetails correct = rococoUserDetailsService.loadUserByUsername("correct");

    final List<SimpleGrantedAuthority> expectedAuthorities = authorityEntities.stream()
        .map(a -> new SimpleGrantedAuthority(a.getAuthority().name()))
        .toList();

    assertEquals(
        "correct",
        correct.getUsername()
    );
    assertEquals(
        "test-pass",
        correct.getPassword()
    );
    assertEquals(
        expectedAuthorities,
        correct.getAuthorities()
    );

    assertTrue(correct.isAccountNonExpired());
    assertTrue(correct.isAccountNonLocked());
    assertTrue(correct.isCredentialsNonExpired());
    assertTrue(correct.isEnabled());
  }

  @Test
  void loadUserByUsernameNegative() {
    final UsernameNotFoundException exception = assertThrows(
        UsernameNotFoundException.class,
        () -> rococoUserDetailsService.loadUserByUsername("incorrect")
    );

    assertEquals(
        "Username: `incorrect` not found",
        exception.getMessage()
    );
  }
}