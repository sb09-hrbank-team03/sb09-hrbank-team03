package com.sb09.hrbank.service;

import com.sb09.hrbank.dto.common.CursorPageResponse;
import com.sb09.hrbank.dto.request.ChangeLogListRequest;
import com.sb09.hrbank.dto.request.EmployeeUpdateRequest;
import com.sb09.hrbank.dto.response.ChangeLogDetailDto;
import com.sb09.hrbank.dto.response.ChangeLogDto;
import com.sb09.hrbank.entity.ChangeLog;
import com.sb09.hrbank.entity.ChangeType;
import java.time.Instant;

public interface ChangeLogService {
  Long getCount(Instant fromDate, Instant toDate);
  ChangeLogDetailDto getDetails(Long id);
  CursorPageResponse<ChangeLogDto> history(ChangeLogListRequest request);
  ChangeLog create(ChangeType changeType, Long id, EmployeeUpdateRequest request);
}
