package com.sb09.hrbank.repository;

import com.sb09.hrbank.dto.request.BackupListRequest;
import com.sb09.hrbank.entity.BackupHistory;
import org.springframework.data.domain.Slice;

public interface BackupRepositoryCustom {

  Slice<BackupHistory> searchBackups(BackupListRequest request);
}
