package timofeyqa.rococo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import timofeyqa.rococo.model.UserJson;
import timofeyqa.rococo.service.api.RestUserdataClient;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final RestUserdataClient userdataClient;

    @Autowired
    public UserController(RestUserdataClient userdataClient) {
        this.userdataClient = userdataClient;
    }

    @GetMapping
    public UserJson getUser(@AuthenticationPrincipal Jwt principal) {
        return userdataClient.getUser(principal.getSubject());
    }


    @PatchMapping
    public UserJson updateUser(@RequestBody UserJson userJson) {
        return userdataClient.updateUser(userJson);
    }
}
