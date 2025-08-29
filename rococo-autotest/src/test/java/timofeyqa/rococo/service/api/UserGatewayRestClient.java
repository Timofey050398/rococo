package timofeyqa.rococo.service.api;

import io.qameta.allure.Step;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.RegisterExtension;
import timofeyqa.rococo.api.core.RestClient;
import timofeyqa.rococo.api.gateway.UserGatewayApi;
import timofeyqa.rococo.jupiter.extension.ApiLoginExtension;
import timofeyqa.rococo.model.rest.UserJson;
import timofeyqa.rococo.service.UserClient;
import timofeyqa.rococo.api.core.ErrorAsserter;

import static timofeyqa.rococo.jupiter.extension.ApiLoginExtension.getToken;
import static timofeyqa.rococo.jupiter.extension.ApiLoginExtension.rest;

public class UserGatewayRestClient extends RestClient implements ErrorAsserter, UserClient {

  private final UserGatewayApi api = create(UserGatewayApi.class);

  @RegisterExtension
  ApiLoginExtension apiLoginExtension = rest();

  @Step("execute GET api/user")
  public UserJson getUser(String bearerToken) {
    return execute(api.getUser(bearerToken));
  }

  @Step("execute PATCH api/user")
  public UserJson updateUser(UserJson user, String bearerToken) {
    return execute(api.updateUser(user, bearerToken));
  }


  @Override
  @Step("Create user")
  public UserJson createUser(String username, String password) {
   throw new UnsupportedOperationException("Not supported yet.");
  }


  @Override
  @Step("Update user")
  public UserJson updateUser(UserJson user) {
    String token = getToken();
    if (!StringUtils.isEmpty(token)) {
      token = "Bearer "+ getToken();
    }
    return updateUser(user, token);
  }

}
