package timofeyqa.rococo.model.rest.pageable;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Sort(
        @JsonProperty boolean empty,
        @JsonProperty boolean sorted,
        @JsonProperty boolean unsorted
) {
}
