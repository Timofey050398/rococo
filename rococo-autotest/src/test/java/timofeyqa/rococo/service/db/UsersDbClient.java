package timofeyqa.rococo.service.db;

import io.qameta.allure.Step;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import timofeyqa.rococo.config.Config;
import timofeyqa.rococo.data.entity.UserEntity;
import timofeyqa.rococo.data.entity.auth.AuthUserEntity;
import timofeyqa.rococo.data.entity.auth.Authority;
import timofeyqa.rococo.data.entity.auth.AuthorityEntity;
import timofeyqa.rococo.data.repository.AuthUserRepository;
import timofeyqa.rococo.data.repository.UserRepository;
import timofeyqa.rococo.data.tpl.XaTransactionTemplate;
import timofeyqa.rococo.model.rest.UserJson;
import timofeyqa.rococo.service.DeletableClient;
import timofeyqa.rococo.service.UserClient;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class UsersDbClient implements UserClient, DeletableClient<UserJson> {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
        CFG.authJdbcUrl(),
        CFG.jdbcUrl()
    );
    private final AuthUserRepository authUserRepository =  new AuthUserRepository();
    private final UserRepository userRepository = new UserRepository();


    @Override
    @Step("Create user with username {username} and password {password}")
    @Nonnull
    public UserJson createUser(String username, String password) {
        UserJson result = xaTransactionTemplate.execute(() -> {
            AuthUserEntity authUserEntity = authUserEntity(username,password);
            authUserRepository.create(authUserEntity);
            return UserJson.fromEntity(
                    userRepository.create(userEntity(username))
                )
                .withPassword(password);
        });

        return Objects.requireNonNull(result, "Result of transaction cannot be null");
    }

    @Override
    @Step("Get user {username}")
    public UserJson getUser(String username){
        return xaTransactionTemplate.execute(() -> UserJson.fromEntity(userRepository.findByUsername(username)
            .orElseThrow()
        ));
    }

    @Override
    @Step("Update user {user}")
    public UserJson updateUser(UserJson user){
        return xaTransactionTemplate.execute(() -> UserJson.fromEntity(userRepository.update(UserEntity.fromJson(user))
        ));
    }

    @Override
    @Step("Delete user {user}")
    public synchronized void remove(UserJson user){
        xaTransactionTemplate.execute(() -> {
            AuthUserEntity authUserEntity = authUserRepository.findByUsername(user.username())
                .orElseThrow();
            authUserRepository.remove(authUserEntity);
            userRepository.remove(UserEntity.fromJson(user));
            return null;
        });
    }

    private AuthUserEntity authUserEntity(String username, String password) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(username);
        authUser.setPassword(pe.encode(password));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        authUser.setAuthorities(
            Arrays.stream(Authority.values()).map(
                e -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUser(authUser);
                    ae.setAuthority(e);
                    return ae;
                }
            ).toList()
        );
        return authUser;
    }

    private UserEntity userEntity(String username) {
        UserEntity ue = new UserEntity();
        ue.setUsername(username);
        return ue;
    }

    @Override
    public void deleteList(List<UUID> uuidList) {
        throw new RuntimeException("Not implemented");
    }
}
