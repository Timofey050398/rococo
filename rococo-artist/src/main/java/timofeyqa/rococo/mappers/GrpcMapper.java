package timofeyqa.rococo.mappers;

import org.springframework.stereotype.Component;
import timofeyqa.grpc.rococo.AddArtistRequest;
import timofeyqa.grpc.rococo.Artist;

@Component
public class GrpcMapper {

  public Artist toArtist(AddArtistRequest request){
    return Artist.newBuilder()
        .setName(request.getName())
        .setBiography(request.getBiography())
        .setPhoto(request.getPhoto())
        .build();
  }
}
