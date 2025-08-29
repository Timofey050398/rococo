package timofeyqa.rococo.service.api;

import io.qameta.allure.Step;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import timofeyqa.rococo.api.core.RestClient;
import timofeyqa.rococo.api.gateway.ArtistGatewayApi;
import timofeyqa.rococo.mapper.ArtistMapper;
import timofeyqa.rococo.model.dto.ArtistDto;
import timofeyqa.rococo.model.rest.ArtistJson;
import timofeyqa.rococo.model.rest.pageable.RestResponsePage;
import timofeyqa.rococo.service.ArtistClient;
import timofeyqa.rococo.api.core.ErrorAsserter;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static timofeyqa.rococo.jupiter.extension.ApiLoginExtension.getToken;

public class ArtistRestClient extends RestClient implements ErrorAsserter, ArtistClient {

  private final ArtistGatewayApi api = create(ArtistGatewayApi.class);

  private static final ArtistMapper MAPPER = ArtistMapper.INSTANCE;

  @Step("execute PATCH /api/artist")
  public ArtistDto updateArtist(ArtistDto Artist, String token) {
    ArtistJson json = execute(api.updateArtist(
        token,
        MAPPER.toJson(Artist)
    ));
    return MAPPER.fromJson(json);
  }

  @Step("execute POST /api/artist")
  public ArtistDto createArtist(ArtistDto Artist, String token) {
    ArtistJson json = execute(api.createArtist(
        token,
        MAPPER.toJson(Artist)
    ));
    return MAPPER.fromJson(json);
  }

  @Step("execute GET /api/artist/{id}")
  public ArtistDto findById(String id) {
    return MAPPER.fromJson(execute(api.getArtist(id)));
  }

  @Step("execute GET /api/artist?page={page}&size={size} with name in query: {name}")
  public RestResponsePage<ArtistDto> getPage(int page, int size, @Nullable String name) {
    RestResponsePage<ArtistJson> json =  execute(api.getPage(page, size, name));
    return Objects.requireNonNull(json).map(MAPPER::fromJson);
  }

  @Override
  @Step("find Artist by title {title}")
  public Optional<ArtistDto> findByName(String title) {
    return getPage(0,1,title).stream().findAny();
  }

  @Override
  @Step("Find all Artists by uuid list")
  public List<ArtistDto> findAllById(List<UUID> uuids) {
    return uuids.stream()
        .map(uuid -> findById(uuid.toString()))
        .toList();
  }

  @Override
  @Step("Create Artist")
  public ArtistDto create(ArtistDto Artist) {
    String token = getToken();
    if (!StringUtils.isEmpty(token)) {
      token = "Bearer "+ getToken();
    }
    return createArtist(Artist, token);
  }

}
