package timofeyqa.rococo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import timofeyqa.rococo.model.UserJson;
import timofeyqa.rococo.service.api.GrpcUserdataClient;

@RestController
public class UserController {

    private final GrpcUserdataClient grpcUserdataClient;

    @Autowired
    public UserController(GrpcUserdataClient grpcUserdataClient) {
        this.grpcUserdataClient = grpcUserdataClient;
    }

    @GetMapping("/api/user")
    public UserJson getUser(@AuthenticationPrincipal Jwt principal) {
        return grpcUserdataClient.getUser(principal.getSubject());
    }
}
