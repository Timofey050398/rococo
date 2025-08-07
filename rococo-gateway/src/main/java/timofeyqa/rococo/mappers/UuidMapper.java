package timofeyqa.rococo.mappers;

import org.springframework.stereotype.Component;
import timofeyqa.grpc.rococo.Uuid;
import timofeyqa.grpc.rococo.UuidList;

import java.util.List;
import java.util.UUID;

@Component
public class UuidMapper {

    public static Uuid fromUuid(UUID uuid){
        return Uuid
            .newBuilder()
            .setUuid(uuid.toString())
            .build();
    }

    public static UuidList fromUuidList(List<UUID> uuidList){
        return UuidList.newBuilder()
                .addAllUuid(uuidList.stream()
                        .map(UuidMapper::fromUuid).
                        toList()
                ).build();
    }
}
