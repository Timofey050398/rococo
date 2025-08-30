package timofeyqa.rococo.service.api;

import io.qameta.allure.Step;
import timofeyqa.rococo.api.core.RestClient;
import timofeyqa.rococo.api.gateway.SessionApi;
import timofeyqa.rococo.model.rest.SessionJson;
import timofeyqa.rococo.api.core.ErrorAsserter;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SessionRestClient extends RestClient implements ErrorAsserter {

  private final SessionApi api = create(SessionApi.class);

  @Step("Execute GET /api/session")
  public SessionJson session(String token){
    return execute(api.session(token));
  }
}
