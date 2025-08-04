package timofeyqa.rococo.controller;

import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import timofeyqa.rococo.model.UserJson;
import timofeyqa.rococo.service.UserService;

@RestController
@RequestMapping("/internal/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public @Nonnull UserJson getUser(@RequestParam String username){
        return userService.getUser(username);
    }

    @PatchMapping
    public @Nonnull UserJson updateUser(@RequestBody UserJson user){
        return userService.patchUser(user);
    }
}
