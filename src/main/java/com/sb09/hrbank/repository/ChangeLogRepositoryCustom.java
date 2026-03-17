package com.sb09.hrbank.repository;

import com.sb09.hrbank.dto.request.ChangeLogListRequest;
import com.sb09.hrbank.entity.ChangeLog;
import org.springframework.data.domain.Slice;

public interface ChangeLogRepositoryCustom {
  Slice<ChangeLog> searchChangeLogs(ChangeLogListRequest request);
}
