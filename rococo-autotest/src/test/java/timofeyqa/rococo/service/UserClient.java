package timofeyqa.rococo.service;

import timofeyqa.rococo.model.rest.UserJson;

public interface UserClient {

  UserJson createUser(String username, String password);

  UserJson getUser(String username);

  UserJson updateUser(UserJson user);
}
