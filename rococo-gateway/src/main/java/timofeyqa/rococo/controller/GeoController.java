package timofeyqa.rococo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import timofeyqa.rococo.model.CountryJson;
import timofeyqa.rococo.model.page.RestPage;
import timofeyqa.rococo.service.api.grpc.GrpcGeoClient;

import java.util.List;

@RestController
public class GeoController {

    private final GrpcGeoClient grpcGeoClient;

    @Autowired
    public GeoController(GrpcGeoClient grpcGeoClient) {
        this.grpcGeoClient = grpcGeoClient;
    }

    @GetMapping("/api/country")
    public RestPage<CountryJson> allCountries(@PageableDefault Pageable pageable){
        List<CountryJson> fullList = grpcGeoClient.allCountries();
        int total = fullList.size();
        int fromIndex = (int) pageable.getOffset();
        int toIndex = Math.min(fromIndex + pageable.getPageSize(), total);

        List<CountryJson> pageContent = fromIndex > total ? List.of() : fullList.subList(fromIndex, toIndex);

        return new RestPage<>(pageContent, pageable, total);
    }
}
