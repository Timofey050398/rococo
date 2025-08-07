package timofeyqa.rococo.mappers;

import org.springframework.stereotype.Component;
import timofeyqa.grpc.rococo.AddMuseumRequest;
import timofeyqa.grpc.rococo.Museum;

@Component
public class GrpcMapper {

  public Museum toMuseum(AddMuseumRequest request){
    return Museum.newBuilder()
        .setTitle(request.getTitle())
        .setDescription(request.getDescription())
        .setCity(request.getCity())
        .setPhoto(request.getPhoto())
        .setCountryId(request.getCountryId())
        .build();
  }
}
