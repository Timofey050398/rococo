package timofeyqa.rococo.model.rest.pageable;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record PageableJson(
    int pageNumber,
    int pageSize,
    SortJson sort,
    long offset,
    boolean paged,
    boolean unpaged
) implements Pageable {

  @Override
  public int getPageNumber() {
    return pageNumber;
  }

  @Override
  public int getPageSize() {
    return pageSize;
  }

  @Override
  public long getOffset() {
    return offset;
  }

  @NotNull
  @Override
  public Sort getSort() {
    return sort;
  }

  @NotNull
  @Override
  public Pageable next() {
    return new PageableJson(pageNumber + 1, pageSize, sort, offset + pageSize, true, false);
  }

  @NotNull
  @Override
  public Pageable previousOrFirst() {
    return hasPrevious() ?
        new PageableJson(pageNumber - 1, pageSize, sort, Math.max(0, offset - pageSize), true, false)
        : first();
  }

  @NotNull
  @Override
  public Pageable first() {
    return new PageableJson(0, pageSize, sort, 0, true, false);
  }

  @NotNull
  @Override
  public Pageable withPage(int pageNumber) {
    return new PageableJson(pageNumber, pageSize, sort, (long) pageNumber * pageSize, true, false);
  }

  @Override
  public boolean hasPrevious() {
    return pageNumber > 0;
  }
}

