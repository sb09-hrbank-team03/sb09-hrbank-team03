package com.sb09.hrbank.service.basic;
import com.sb09.hrbank.dto.common.CursorPageResponse;
import com.sb09.hrbank.dto.request.ChangeLogListRequest;
import com.sb09.hrbank.dto.response.ChangeLogDetailDto;
import com.sb09.hrbank.service.ChangeLogService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicChangeLogService implements ChangeLogService {

  @Override
  public Long getCount(Instant fromDate, Instant toDate) {
    return 0L;
  }

  @Override
  public ChangeLogDetailDto getDetails(Long id) {
    return null;
  }

  @Override
  public CursorPageResponse history(ChangeLogListRequest request) {
    return null;
  }
}
