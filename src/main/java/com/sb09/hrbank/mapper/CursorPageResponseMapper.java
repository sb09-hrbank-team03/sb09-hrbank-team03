package com.sb09.hrbank.mapper;

import com.sb09.hrbank.dto.common.CursorPageResponse;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CursorPageResponseMapper {

  public <T, R> CursorPageResponse<R> fromSlice(Slice<T> slice, Function<T, R> converter,
      Function<T, String> cursorExtractor, Function<T, Long> idExtractor, Long totalElements) {
    List<R> dtos = slice.getContent().stream()
        .map(converter)
        .toList();
    String nextCursor = null;
    Long nextIdAfter = null;
    if (slice.hasNext() && !slice.getContent().isEmpty()) {
      T last = slice.getContent().get(slice.getContent().size() - 1);
      nextCursor = cursorExtractor.apply(last);
      nextIdAfter = idExtractor.apply(last);
    }
    return new CursorPageResponse<>(
        dtos,
        nextCursor,
        nextIdAfter,
        slice.getSize(),
        totalElements,
        slice.hasNext()
    );
  }
}