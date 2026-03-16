package com.sb09.hrbank.service.basic;

import com.sb09.hrbank.dto.common.CursorPageResponse;
import com.sb09.hrbank.dto.request.ChangeLogListRequest;
import com.sb09.hrbank.dto.request.EmployeeUpdateRequest;
import com.sb09.hrbank.dto.response.ChangeLogDetailDto;
import com.sb09.hrbank.dto.response.ChangeLogDto;
import com.sb09.hrbank.dto.response.DiffDto;
import com.sb09.hrbank.entity.ChangeLog;
import com.sb09.hrbank.entity.ChangeLogDetail;
import com.sb09.hrbank.entity.ChangeType;
import com.sb09.hrbank.entity.Employee;
import com.sb09.hrbank.mapper.ChangeLogDetailMapper;
import com.sb09.hrbank.mapper.ChangeLogMapper;
import com.sb09.hrbank.mapper.CursorPageResponseMapper;
import com.sb09.hrbank.repository.ChangeLogDetailRepository;
import com.sb09.hrbank.repository.ChangeLogRepository;
import com.sb09.hrbank.repository.ChangeLogSpecification;
import com.sb09.hrbank.repository.EmployeeRepository;
import com.sb09.hrbank.service.ChangeLogService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
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
  private final ChangeLogDetailMapper changeLogDetailMapper;

  @Transactional(readOnly = true)
  @Override
  public Long getCount(Instant fromDate, Instant toDate) {
    if (toDate == null) {
      // 기본 오늘
      toDate = Instant.now();
    }
    if (fromDate == null) {
      // 기본 일주일 전
      fromDate = toDate.minus(7, ChronoUnit.DAYS);
    }
    return changeLogRepository.countByCreatedAtBetween(fromDate, toDate);
  }

  @Transactional(readOnly = true)
  @Override
  public ChangeLogDetailDto getDetails(Long id) {
    ChangeLog changeLog = changeLogRepository.findById(id).orElseThrow(() -> new NoSuchElementException("해당 id의 수정 이력이 존재하지 않습니다."));
    List<ChangeLogDetail> diffs = changeLogDetailRepository.findByChangeLogId(id);

    List<DiffDto> diffDtos = changeLogDetailMapper.toDiffDtoList(diffs);
    return changeLogDetailMapper.toDetailDto(changeLog,diffDtos);
  }

  @Transactional(readOnly = true)
  @Override
  public CursorPageResponse<ChangeLogDto> history(ChangeLogListRequest request) {
    Sort sort;
    String sortField = request.getSortField() != null ? request.getSortField() : "at";
    String sortDirection = request.getSortDirection() != null ? request.getSortDirection() : "desc";
    Integer size = request.getSize() != null ? request.getSize() : 10;
    if (sortField.equals("at")) {
      sort = Sort.by(Sort.Direction.fromString(sortDirection), "createdAt")
          .and(Sort.by(Sort.Direction.DESC, "id"));
    } else {
      sort = Sort.by(Sort.Direction.fromString(sortDirection), "ipAddress")
          .and(Sort.by(Sort.Direction.DESC, "id"));
    }
    Pageable pageable = PageRequest.of(0, size, sort);
    Slice<ChangeLog> logSlice;
    ChangeType type = null;
    if (request.getType() != null && !request.getType().isEmpty()) {
      type = ChangeType.valueOf(request.getType());
    }
    logSlice = changeLogRepository.findAll(
        ChangeLogSpecification.build(request),
        pageable
    );

    return cursorPageResponseMapper.fromSlice(
        logSlice,
        log -> changeLogMapper.toDto(log),
        log -> {
          if (sortField.equals("at")) {
            return log.getCreatedAt();
          }

          return log.getIpAddress();
        },
        log -> log.getId()
    );
  }

  @Override
  public ChangeLog create(ChangeType changeType, Employee employee, String ipAddress, EmployeeUpdateRequest request) {
    String memo = request.memo();
    String employeeNumber = employee.getEmployeeNumber();
    ChangeLog changeLog = new ChangeLog(changeType, employee, ipAddress, memo, employeeNumber);
    ChangeLog saved = changeLogRepository.save(changeLog);

    List<ChangeLogDetail> changeLogDetail = new ArrayList<>();
    addDetail(changeType, changeLogDetail, changeLog, request);
    changeLogDetailRepository.saveAll(changeLogDetail);
    return saved;
  }

  @Override
  public void addDetail(ChangeType changeType, List<ChangeLogDetail> changeLogDetail, ChangeLog changeLog,
      EmployeeUpdateRequest request) {
    if(changeType.equals(ChangeType.CREATED)){

    }
    else if(changeType.equals(ChangeType.DELETED)){

    }
    else{

    }
  }
}
