package timofeyqa.rococo.service.utils;

import lombok.experimental.UtilityClass;
import timofeyqa.grpc.rococo.Uuid;
import timofeyqa.grpc.rococo.UuidList;

import java.util.List;
import java.util.UUID;

@UtilityClass
public class UuidUtil {

    public static Uuid fromUuid(UUID uuid){
        return Uuid.newBuilder().setUuid(uuid.toString()).build();
    }

    public static UuidList fromUuidList(List<UUID> uuidList){
        return UuidList.newBuilder()
                .addAllUuid(uuidList.stream()
                        .map(UuidUtil::fromUuid).
                        toList()
                ).build();
    }
}
