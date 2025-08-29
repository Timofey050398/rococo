package timofeyqa.rococo.model.rest.pageable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RestResponsePage<T> extends PageImpl<T> implements Serializable {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RestResponsePage(
        @JsonProperty("content") List<T> content,
        @JsonProperty("number") int number,
        @JsonProperty("size") int size,
        @JsonProperty("totalElements") long totalElements,
        @JsonProperty("pageable") PageableJson pageable,
        @JsonProperty("last") boolean last,
        @JsonProperty("totalPages") int totalPages,
        @JsonProperty("sort") SortJson sort,
        @JsonProperty("first") boolean first,
        @JsonProperty("numberOfElements") int numberOfElements,
        @JsonProperty("empty") boolean empty
    ) {
        super(content, pageable, totalElements);
    }

    public RestResponsePage(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public RestResponsePage(List<T> content) {
        super(content);
    }

    public RestResponsePage() {
        super(new ArrayList<>());
    }

    @NotNull
    @Override
    public <U> RestResponsePage<U> map(@NotNull Function<? super T, ? extends U> converter) {
        List<U> convertedContent = this.getContent().stream()
            .map(converter)
            .collect(Collectors.toList());

        return new RestResponsePage<>(
            convertedContent,
            this.getPageable(),
            this.getTotalElements()
        );
    }
}
