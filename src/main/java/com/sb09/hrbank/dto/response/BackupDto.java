package com.sb09.hrbank.dto.response;

import com.sb09.hrbank.entity.BackupStatus;
import java.time.Instant;

public record BackupDto(
    Long id,
    String worker,
    Instant startedAt,
    Instant endedAt,
    BackupStatus status,
    Long fileId
) {}