package com.sb09.hrbank.service;

import com.sb09.hrbank.dto.common.CursorPageResponse;
import com.sb09.hrbank.dto.request.ChangeLogListRequest;
import com.sb09.hrbank.dto.request.EmployeeUpdateRequest;
import com.sb09.hrbank.dto.response.ChangeLogDetailDto;
import com.sb09.hrbank.dto.response.ChangeLogDto;
import com.sb09.hrbank.entity.ChangeLog;
import com.sb09.hrbank.entity.ChangeLogDetail;
import com.sb09.hrbank.entity.ChangeType;
import com.sb09.hrbank.entity.Employee;
import java.time.Instant;
import java.util.List;

public interface ChangeLogService {
  Long getCount(Instant fromDate, Instant toDate);
  ChangeLogDetailDto getDetails(Long id);
  CursorPageResponse<ChangeLogDto> history(ChangeLogListRequest request);
  ChangeLog createByCreate(Employee employee, String ipAddress, String memo);
  ChangeLog createByUpdate(Employee employee, String ipAddress, EmployeeUpdateRequest request);
  ChangeLog createByDelete(Employee employee, String ipAddress);
}
