package timofeyqa.rococo.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import timofeyqa.rococo.model.UserJson;
import timofeyqa.rococo.service.api.RestUserdataClient;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final RestUserdataClient userdataClient;

    @Autowired
    public UserController(RestUserdataClient userdataClient) {
        this.userdataClient = userdataClient;
    }

    @GetMapping
    public UserJson getUser(@AuthenticationPrincipal Jwt principal) {
        final String username = principal.getSubject();
        LOG.info("try to get user {}", username);
        return userdataClient.getUser(username);
    }


    @PatchMapping
    public UserJson updateUser(@RequestBody @Valid UserJson userJson, @AuthenticationPrincipal Jwt principal) {
        return userdataClient.updateUser(userJson, principal.getSubject());
    }
}
