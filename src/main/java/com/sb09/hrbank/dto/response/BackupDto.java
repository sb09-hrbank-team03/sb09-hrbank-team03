package com.sb09.hrbank.dto.response;

import com.sb09.hrbank.entity.BackupStatus;

public record BackupDto(
    Long id,
    String worker,
    String startedAt,
    String endedAt,
    BackupStatus status,
    Long fileId
) {}