package timofeyqa.rococo.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import timofeyqa.rococo.data.entity.PaintingEntity;

import java.util.Arrays;
import java.util.UUID;

import static timofeyqa.rococo.utils.ToEntityPhotoConverter.convert;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(toBuilder = true)
public record PaintingJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("title")
    String title,
    @JsonProperty("description")
    String description,
    @JsonProperty("artist")
    ArtistJson artist,
    @JsonProperty("museum")
    MuseumJson museum,
    @JsonProperty("content")
    String content) implements ContentImpl {

    public static PaintingJson fromEntity(PaintingEntity paintingEntity) {
        MuseumJson museumJson = paintingEntity.getMuseum() == null
            ? null
            : MuseumJson.fromEntity(paintingEntity.getMuseum());

        return new PaintingJson(
            paintingEntity.getId(),
            paintingEntity.getTitle(),
            paintingEntity.getDescription(),
            ArtistJson.fromEntity(paintingEntity.getArtist()),
            museumJson,
            Arrays.toString(paintingEntity.getContent())
        );
    }

    public PaintingEntity toEntity() {
        PaintingEntity paintingEntity = new PaintingEntity();
        paintingEntity.setId(id);
        paintingEntity.setTitle(title);
        paintingEntity.setDescription(description);
        paintingEntity.setArtist(artist.toEntity());
        if(museum != null) {
            paintingEntity.setMuseum(museum.toEntity());
        }
        paintingEntity.setContent(convert(content));
        return paintingEntity;
    }
}
