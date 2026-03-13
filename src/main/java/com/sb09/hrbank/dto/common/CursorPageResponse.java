package com.sb09.hrbank.dto.common;

import java.util.List;

public record CursorPageResponse<T>(
    List<T> content,
    Object nextCursor,
    Long nextIdAfter,
    int size,
    Long totalElements,
    boolean hasNext
) {

}
