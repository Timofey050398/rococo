package timofeyqa.rococo.controller;

import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import timofeyqa.rococo.model.UserJson;
import timofeyqa.rococo.service.UserService;

@RestController
@RequestMapping("/internal/api/user")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public @Nonnull UserJson getUser(@RequestParam String username){
        LOG.info("try to get user {}", username);
        return userService.getUser(username);
    }

    @PatchMapping
    public @Nonnull UserJson updateUser(@RequestBody UserJson user, @RequestParam String username){
        return userService.patchUser(user, username);
    }
}
