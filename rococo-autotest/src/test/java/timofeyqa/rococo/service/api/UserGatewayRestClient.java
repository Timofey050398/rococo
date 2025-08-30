package timofeyqa.rococo.service.api;

import io.qameta.allure.Step;
import org.apache.commons.lang3.StringUtils;
import timofeyqa.rococo.api.core.RestClient;
import timofeyqa.rococo.api.gateway.UserGatewayApi;
import timofeyqa.rococo.model.rest.UserJson;
import timofeyqa.rococo.service.UserClient;
import timofeyqa.rococo.api.core.ErrorAsserter;

import javax.annotation.ParametersAreNonnullByDefault;

import static timofeyqa.rococo.jupiter.extension.ApiLoginExtension.getToken;

@ParametersAreNonnullByDefault
public class UserGatewayRestClient extends RestClient implements ErrorAsserter, UserClient {

  private final UserGatewayApi api = create(UserGatewayApi.class);

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
