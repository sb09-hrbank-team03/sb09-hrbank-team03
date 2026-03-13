package com.sb09.hrbank.dto.response;

import com.sb09.hrbank.entity.ChangeType;
import java.time.Instant;

public record ChangeLogDto(
    Long id,
    ChangeType type,
    String employeeNumber,
    String memo,
    String ipAddress,
    Instant at
) {
}
