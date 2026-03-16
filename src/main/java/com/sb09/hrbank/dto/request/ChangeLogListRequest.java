package com.sb09.hrbank.dto.request;

import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public record ChangeLogListRequest(
    String employeeNumber,
    String type,
    String memo,
    String ipAddress,
    Instant atFrom,
    Instant atTo,
    Long idAfter,
    String cursor,
    Integer size,
    String sortField,
    String sortDirection
) {

}
