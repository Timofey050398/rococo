package timofeyqa.rococo.jupiter.extension;

import org.junit.jupiter.api.extension.*;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.model.rest.ContentImpl;
import timofeyqa.rococo.service.ArtistClient;
import timofeyqa.rococo.service.DeletableClient;
import timofeyqa.rococo.service.MuseumClient;
import timofeyqa.rococo.service.PaintingClient;
import timofeyqa.rococo.service.db.ArtistDbClient;
import timofeyqa.rococo.service.db.MuseumDbClient;
import timofeyqa.rococo.service.db.PaintingDbClient;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
      if (paintingClient instanceof DeletableClient deletable) {
        deletable.deleteList(contentUuids(content.paintings()));
      }
      if (museumClient instanceof DeletableClient deletable) {
        deletable.deleteList(contentUuids(content.allMuseums()));
      }
      if (artistClient instanceof DeletableClient deletable) {
        deletable.deleteList(contentUuids(content.allArtists()));
      }
    }
  }

  private <T extends ContentImpl> List<UUID> contentUuids(Set<T> contentList){
    return contentList.stream()
        .map(ContentImpl::id)
        .toList();
  }
}
