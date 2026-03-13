package com.sb09.hrbank.service;

import com.sb09.hrbank.dto.common.CursorPageResponse;
import com.sb09.hrbank.dto.request.ChangeLogListRequest;
import com.sb09.hrbank.dto.response.ChangeLogDetailDto;
import java.time.Instant;

public interface ChangeLogService {
  Long getCount(Instant fromDate, Instant toDate);
  ChangeLogDetailDto getDetails(Long id);
  CursorPageResponse history(ChangeLogListRequest request);
}
