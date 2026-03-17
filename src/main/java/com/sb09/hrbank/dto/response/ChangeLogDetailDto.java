package com.sb09.hrbank.dto.response;

import com.sb09.hrbank.entity.ChangeType;
import java.time.Instant;
import java.util.List;

public record ChangeLogDetailDto (
    Long id,
    ChangeType type,
    String employeeNumber,
    String memo,
    String ipAddress,
    Instant at,
    String employeeName,
    Long profileImageId,
    List<DiffDto> diffs
)
{ }
