package timofeyqa.rococo.model.rest.pageable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.data.domain.Sort;

import java.util.List;

@Getter
public final class SortJson extends Sort {

  private final boolean empty;
  private final boolean sorted;
  private final boolean unsorted;

  @JsonCreator
  public SortJson(
      @JsonProperty("empty") boolean empty,
      @JsonProperty("sorted") boolean sorted,
      @JsonProperty("unsorted") boolean unsorted
  ) {
    super(List.of());
    this.empty = empty;
    this.sorted = sorted;
    this.unsorted = unsorted;
  }
}
