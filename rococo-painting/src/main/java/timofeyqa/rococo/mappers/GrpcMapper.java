package timofeyqa.rococo.mappers;

import org.springframework.stereotype.Component;
import timofeyqa.grpc.rococo.AddPaintingRequest;
import timofeyqa.grpc.rococo.Painting;

@Component
public class GrpcMapper {

  public Painting toPainting(AddPaintingRequest request){
    return Painting.newBuilder()
        .setTitle(request.getTitle())
        .setDescription(request.getDescription())
        .setArtistId(request.getArtistId())
        .setMuseumId(request.getMuseumId())
        .setContent(request.getContent())
        .build();
  }
}
