package timofeyqa.rococo.model.rest.pageable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Sort;

import java.util.List;

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

  public boolean isEmpty() {
    return empty;
  }

  public boolean isSorted() {
    return sorted;
  }

  public boolean isUnsorted() {
    return unsorted;
  }
}
