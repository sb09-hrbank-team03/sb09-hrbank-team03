package com.sb09.hrbank.service.basic;
import com.sb09.hrbank.dto.common.CursorPageResponse;
import com.sb09.hrbank.dto.request.ChangeLogListRequest;
import com.sb09.hrbank.dto.request.EmployeeUpdateRequest;
import com.sb09.hrbank.dto.response.ChangeLogDetailDto;
import com.sb09.hrbank.dto.response.ChangeLogDto;
import com.sb09.hrbank.entity.ChangeLog;
import com.sb09.hrbank.entity.ChangeType;
import com.sb09.hrbank.mapper.ChangeLogMapper;
import com.sb09.hrbank.mapper.CursorPageResponseMapper;
import com.sb09.hrbank.repository.ChangeLogDetailRepository;
import com.sb09.hrbank.repository.ChangeLogRepository;
import com.sb09.hrbank.service.ChangeLogService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicChangeLogService implements ChangeLogService {
  private final ChangeLogRepository changeLogRepository;
  private final ChangeLogDetailRepository changeLogDetailRepository;
  private final CursorPageResponseMapper cursorPageResponseMapper;
  private final ChangeLogMapper changeLogMapper;

  @Transactional(readOnly = true)
  @Override
  public Long getCount(Instant fromDate, Instant toDate) {
    if(fromDate == null){
      // 기본 일주일 전
      fromDate = toDate.minus(7, ChronoUnit.DAYS);
    }
    if(toDate == null){
      // 기본 오늘
      toDate = Instant.now();
    }
    return changeLogRepository.countByCreatedAtBetween(fromDate, toDate);
  }

  @Transactional(readOnly = true)
  @Override
  public ChangeLogDetailDto getDetails(Long id) {
    return null;
  }

  @Transactional(readOnly = true)
  @Override
  public CursorPageResponse<ChangeLogDto> history(ChangeLogListRequest request) {
    Sort sort;
    if(request.getSortField().equals("ipAddress")){
      sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), "ipAddress")
          .and(Sort.by(Sort.Direction.DESC, "id"));
    }
    else{
      sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), "createdAt")
          .and(Sort.by(Sort.Direction.DESC, "id"));
    }
    Pageable pageable = PageRequest.of(0, request.getSize(), sort);
    Slice<ChangeLog> logSlice = changeLogRepository.findAllByCondition(
        request.getEmployeeNumber(),
        request.getMemo(),
        request.getIpAddress(),
        request.getType(),
        request.getAtFrom(),
        request.getAtTo(),
        request.getIdAfter(),
        pageable
    );

    return cursorPageResponseMapper.fromSlice(
        logSlice,
        log -> changeLogMapper.toDto(log),
        log -> {
          if(request.getSortField().equals("ipAddress")){
            return log.getIpAddress();
          }
          return log.getCreatedAt();
        },
          log -> log.getId()
    );
  }

  @Override
  public ChangeLog create(ChangeType changeType, Long id, EmployeeUpdateRequest request) {
    return null;
  }
}
