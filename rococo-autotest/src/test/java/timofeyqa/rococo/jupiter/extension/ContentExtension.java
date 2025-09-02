package timofeyqa.rococo.jupiter.extension;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.*;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.model.rest.ContentImpl;
import timofeyqa.rococo.service.ArtistClient;
import timofeyqa.rococo.service.db.DeletableClient;
import timofeyqa.rococo.service.MuseumClient;
import timofeyqa.rococo.service.PaintingClient;
import timofeyqa.rococo.service.db.ArtistDbClient;
import timofeyqa.rococo.service.db.MuseumDbClient;
import timofeyqa.rococo.service.db.PaintingDbClient;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class ContentExtension implements ParameterResolver, AfterEachCallback {
  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ContentExtension.class);

  private final ArtistClient artistClient = new ArtistDbClient();
  private final MuseumClient museumClient = new MuseumDbClient();
  private final PaintingClient paintingClient = new PaintingDbClient();

  public static ContentJson content() {
    final ExtensionContext context = TestMethodContextExtension.context();
    ContentJson content = context.getStore(NAMESPACE).get(context.getUniqueId(), ContentJson.class);
    if (content == null) {
      content = new ContentJson(
          new HashSet<>(),
          new HashSet<>(),
          new HashSet<>()
      );
      context.getStore(NAMESPACE).put(context.getUniqueId(), content);
    }
    return content;
  }


  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(ContentJson.class);
  }

  @Override
  public ContentJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), ContentJson.class);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public void afterEach(ExtensionContext context) {
    ContentJson content = context.getStore(NAMESPACE).get(context.getUniqueId(), ContentJson.class);
    if (content != null) {
      Allure.step("Post condition: delete content",()-> {
        var paintings = content.paintings();
        if (paintingClient instanceof DeletableClient deletable
            && !paintings.isEmpty()) {
          deletable.deleteList(contentUuids(paintings));
        }
        var museums = content.allMuseums();
        if (museumClient instanceof DeletableClient deletable
            && !museums.isEmpty()) {
          deletable.deleteList(contentUuids(museums));
        }
        var artists = content.allArtists();
        if (artistClient instanceof DeletableClient deletable
            && !artists.isEmpty()) {
          deletable.deleteList(contentUuids(artists));
        }
      });
    }
  }

  private <T extends ContentImpl> List<UUID> contentUuids(Set<T> contentList){
    return contentList.stream()
        .map(ContentImpl::id)
        .toList();
  }
}
