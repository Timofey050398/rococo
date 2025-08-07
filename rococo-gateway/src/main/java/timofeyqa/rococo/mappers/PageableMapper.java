package timofeyqa.rococo.mappers;

import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class PageableMapper {

    public static timofeyqa.grpc.rococo.Pageable toGrpcPageable(@Nonnull Pageable pageable, String filterField) {
        var builder = timofeyqa.grpc.rococo.Pageable.newBuilder()
            .setPage(pageable.getPageNumber())
            .setSize(pageable.getPageSize());
        if (filterField != null) {
            builder.setFilterField(filterField);
        }
        return builder.build();
    }
}
