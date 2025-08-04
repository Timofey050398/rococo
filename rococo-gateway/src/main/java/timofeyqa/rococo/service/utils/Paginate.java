package timofeyqa.rococo.service.utils;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Pageable;
import timofeyqa.rococo.model.page.RestPage;

import java.util.List;

@UtilityClass
public class Paginate {

    public static timofeyqa.grpc.rococo.Pageable toGrpcPageable(Pageable pageable) {
        return timofeyqa.grpc.rococo.Pageable.newBuilder()
                .setPage(pageable.getPageNumber())
                .setSize(pageable.getPageSize())
                .build();
    }
}
