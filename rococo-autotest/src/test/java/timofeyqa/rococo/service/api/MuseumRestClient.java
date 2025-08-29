package timofeyqa.rococo.service.api;

import io.qameta.allure.Step;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import timofeyqa.rococo.api.core.RestClient;
import timofeyqa.rococo.api.gateway.MuseumGatewayApi;
import timofeyqa.rococo.mapper.MuseumMapper;
import timofeyqa.rococo.model.dto.MuseumDto;
import timofeyqa.rococo.model.rest.MuseumJson;
import timofeyqa.rococo.model.rest.pageable.RestResponsePage;
import timofeyqa.rococo.service.MuseumClient;
import timofeyqa.rococo.api.core.ErrorAsserter;

import java.util.*;

import static timofeyqa.rococo.jupiter.extension.ApiLoginExtension.getToken;

public class MuseumRestClient extends RestClient implements ErrorAsserter, MuseumClient {

  private final MuseumGatewayApi api = create(MuseumGatewayApi.class);

  private static final MuseumMapper MAPPER = MuseumMapper.INSTANCE;

  @Step("execute PATCH /api/museum")
  public MuseumDto updateMuseum(MuseumDto Museum, String token) {
    MuseumJson json = execute(api.updateMuseum(
        token,
        MAPPER.toJson(Museum)
    ));
    return MAPPER.fromJson(json);
  }

  @Step("execute POST /api/museum")
  public MuseumDto createMuseum(MuseumDto Museum, String token) {
    MuseumJson json = execute(api.createMuseum(
        token,
        MAPPER.toJson(Museum)
    ));
    return MAPPER.fromJson(json);
  }

  @Step("execute GET /api/museum/{id}")
  public MuseumDto findById(String id) {
    return MAPPER.fromJson(execute(api.getMuseum(id)));
  }

  @Step("execute GET /api/museum?page={page}&size={size} with title in query: {title}")
  public RestResponsePage<MuseumDto> getPage(int page, int size, @Nullable String title) {
    RestResponsePage<MuseumJson> json =  execute(api.getPage(page, size, title));
    return Objects.requireNonNull(json).map(MAPPER::fromJson);
  }

  @Override
  @Step("find Museum by title {title}")
  public Optional<MuseumDto> findByTitle(String title) {
    return getPage(0,1,title).stream().findAny();
  }

  @Override
  @Step("Find all Museums by uuid list")
  public List<MuseumDto> findAllById(List<UUID> uuids) {
    return uuids.stream()
        .map(uuid -> findById(uuid.toString()))
        .toList();
  }

  @Override
  @Step("Create Museum")
  public MuseumDto create(MuseumDto Museum) {
    String token = getToken();
    if (!StringUtils.isEmpty(token)) {
      token = "Bearer "+ getToken();
    }
    return createMuseum(Museum, token);
  }
}
