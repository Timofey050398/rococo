package timofeyqa.rococo.service.api;

import io.qameta.allure.Step;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import timofeyqa.rococo.api.core.RestClient;
import timofeyqa.rococo.api.gateway.PaintingGatewayApi;
import timofeyqa.rococo.mapper.PaintingMapper;
import timofeyqa.rococo.model.dto.PaintingDto;
import timofeyqa.rococo.model.rest.PaintingJson;
import timofeyqa.rococo.model.rest.pageable.RestResponsePage;
import timofeyqa.rococo.service.PaintingClient;
import timofeyqa.rococo.api.core.ErrorAsserter;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

import static timofeyqa.rococo.jupiter.extension.ApiLoginExtension.getToken;

@ParametersAreNonnullByDefault
public class PaintingRestClient extends RestClient implements ErrorAsserter, PaintingClient {

  private final PaintingGatewayApi api = create(PaintingGatewayApi.class);

  private static final PaintingMapper MAPPER = PaintingMapper.INSTANCE;

  @Step("execute PATCH /api/painting")
  public PaintingDto updatePainting(PaintingDto painting, String token) {
    PaintingJson json = execute(api.updatePainting(
        token,
        MAPPER.toJson(painting)
    ));
    return MAPPER.fromJson(json);
  }

  @Step("execute POST /api/painting")
  public PaintingDto createPainting(PaintingDto painting, String token) {
    PaintingJson json = execute(api.createPainting(
        token,
        MAPPER.toJson(painting)
    ));
    return MAPPER.fromJson(json);
  }

  @Step("execute GET /api/painting/{id}")
  public PaintingDto findById(String id) {
    return MAPPER.fromJson(execute(api.getPainting(id)));
  }

  @Step("execute GET /api/painting/artist/{artistId}?page={page}&size={size}")
  public RestResponsePage<PaintingDto> getPageByArtist(String artistId, int page, int size) {
    RestResponsePage<PaintingJson> json = execute(api.getPageByArtist(artistId, page, size));
    return Objects.requireNonNull(json).map(MAPPER::fromJson);
  }

  @Step("execute GET /api/painting?page={page}&size={size} with title in query: {title}")
  public RestResponsePage<PaintingDto> getPage(int page, int size, @Nullable String title) {
    RestResponsePage<PaintingJson> json =  execute(api.getPage(page, size, title));
    return Objects.requireNonNull(json).map(MAPPER::fromJson);
  }

  @Override
  @Step("find painting by title {title}")
  public Optional<PaintingDto> findByTitle(String title) {
    return getPage(0,1,title).stream().findAny();
  }

  @Override
  @Step("Find all paintings by uuid list")
  public List<PaintingDto> findAllById(List<UUID> uuids) {
    return uuids.stream()
        .map(uuid -> findById(uuid.toString()))
        .toList();
  }

  @Override
  @Step("find all paintings by artist Id.")
  public List<PaintingDto> findAllByArtistId(UUID artistId) {
    final int pageSize = 10;
    int pageNumber = 0;
    List<PaintingDto> paintings = new ArrayList<>();

    RestResponsePage<PaintingDto> page;
    do {
      page = getPageByArtist(artistId.toString(), pageNumber, pageSize);
      paintings.addAll(page.getContent());
      pageNumber++;
    } while (!page.isLast());

    return paintings;
  }

  @Override
  @Step("Create painting")
  public PaintingDto create(PaintingDto painting) {
    String token = getToken();
    if (!StringUtils.isEmpty(token)) {
      token = "Bearer "+ getToken();
    }
    return createPainting(painting, token);
  }
}
