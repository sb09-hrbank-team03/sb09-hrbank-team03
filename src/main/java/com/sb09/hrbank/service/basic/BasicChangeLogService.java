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
import com.sb09.hrbank.entity.Department;
import com.sb09.hrbank.entity.Employee;
import com.sb09.hrbank.mapper.ChangeLogDetailMapper;
import com.sb09.hrbank.mapper.ChangeLogMapper;
import com.sb09.hrbank.mapper.CursorPageResponseMapper;
import com.sb09.hrbank.repository.ChangeLogDetailRepository;
import com.sb09.hrbank.repository.ChangeLogRepository;
import com.sb09.hrbank.repository.DepartmentRepository;
import com.sb09.hrbank.service.ChangeLogService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicChangeLogService implements ChangeLogService {

  private final ChangeLogRepository changeLogRepository;
  private final ChangeLogDetailRepository changeLogDetailRepository;
  private final DepartmentRepository departmentRepository;
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
    ChangeLog changeLog = changeLogRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 id의 수정 이력이 존재하지 않습니다."));
    List<ChangeLogDetail> diffs = changeLogDetailRepository.findByChangeLogId(id);

    List<DiffDto> diffDtos = changeLogDetailMapper.toDiffDtoList(diffs);
    return changeLogDetailMapper.toDetailDto(changeLog, diffDtos);
  }

  @Transactional(readOnly = true)
  @Override
  public CursorPageResponse<ChangeLogDto> history(ChangeLogListRequest request) {
    var slice = changeLogRepository.searchChangeLogs(request);
    Long totalElements = changeLogRepository.count();
    return cursorPageResponseMapper.fromSlice(
        slice,
        changeLogMapper::toDto,
        ChangeLog::getCreatedAt,
        ChangeLog::getId,
        totalElements
    );
  }

  // 추가 후에 실행
  @Override
  public ChangeLog createByCreate(Employee employee, String ipAddress, String memo) {
    String employeeNumber = employee.getEmployeeNumber();
    ChangeLog changeLog = new ChangeLog(ChangeType.CREATED, employee, ipAddress, memo,
        employeeNumber);
    ChangeLog saved = changeLogRepository.save(changeLog);

    List<ChangeLogDetail> details = new ArrayList<>();
    addByCreate(details, employee, changeLog);
    changeLogDetailRepository.saveAll(details);
    return saved;
  }

  // 업데이트 전에 실행
  @Override
  public ChangeLog createByUpdate(Employee employee, String ipAddress,
      EmployeeUpdateRequest request) {
    String memo = request.memo();
    String employeeNumber = employee.getEmployeeNumber();
    ChangeLog changeLog = new ChangeLog(ChangeType.UPDATED, employee, ipAddress, memo,
        employeeNumber);
    ChangeLog saved = changeLogRepository.save(changeLog);

    List<ChangeLogDetail> details = new ArrayList<>();
    addByUpdate(details, employee, request, changeLog);
    changeLogDetailRepository.saveAll(details);
    return saved;
  }

  // 삭제 전에 실행
  @Override
  public ChangeLog createByDelete(Employee employee, String ipAddress) {
    String employeeNumber = employee.getEmployeeNumber();
    ChangeLog changeLog = new ChangeLog(ChangeType.DELETED, null, ipAddress, null, employeeNumber);
    ChangeLog saved = changeLogRepository.save(changeLog);

    List<ChangeLogDetail> details = new ArrayList<>();
    addByDelete(details, employee, changeLog);
    changeLogDetailRepository.saveAll(details);
    return saved;
  }

  private void addByCreate(List<ChangeLogDetail> details, Employee employee, ChangeLog changeLog) {
    addDetail(details, changeLog, "hireDate", null, employee.getHireDate().toString());
    addDetail(details, changeLog, "name", null, employee.getName());
    addDetail(details, changeLog, "position", null, employee.getPosition());
    addDetail(details, changeLog, "department", null, employee.getDepartment().getName());
    addDetail(details, changeLog, "email", null, employee.getEmail());
    addDetail(details, changeLog, "employeeNumber", null, employee.getEmployeeNumber());
    addDetail(details, changeLog, "status", null, employee.getStatus().toString());
  }

  private void addByUpdate(List<ChangeLogDetail> details, Employee employee,
      EmployeeUpdateRequest request, ChangeLog changeLog) {
    if (request.hireDate() != null && !employee.getHireDate().equals(request.hireDate())) {
      addDetail(details, changeLog, "hireDate", employee.getHireDate().toString(),
          request.hireDate().toString());
    }
    if (request.name() != null && !employee.getName().equals(request.name())) {
      addDetail(details, changeLog, "name", employee.getName(), request.name());
    }
    if (request.position() != null && !employee.getPosition().equals(request.position())) {
      addDetail(details, changeLog, "position", employee.getPosition(), request.position());
    }
    if (request.departmentId() != null) {
      Department department = departmentRepository.findById(request.departmentId())
          .orElseThrow(() -> new NoSuchElementException("id에 해당하는 부서가 존재하지 않습니다."));
      String departmentName = department.getName();
      if (!departmentName.equals(employee.getDepartment().getName())) {
        addDetail(details, changeLog, "department", employee.getDepartment().getName(), departmentName);
      }
    }
    if (request.email() != null && !employee.getEmail().equals(request.email())) {
      addDetail(details, changeLog, "email", employee.getEmail(), request.email());
    }
    if (request.status() != null && !employee.getStatus().equals(request.status())) {
      addDetail(details, changeLog, "status", employee.getStatus().toString(),
          request.status().toString());
    }
  }

  private void addByDelete(List<ChangeLogDetail> details, Employee employee, ChangeLog changeLog) {
    addDetail(details, changeLog, "hireDate", employee.getHireDate().toString(), null);
    addDetail(details, changeLog, "name", employee.getName(), null);
    addDetail(details, changeLog, "position", employee.getPosition(), null);
    addDetail(details, changeLog, "department", employee.getDepartment().getName(), null);
    addDetail(details, changeLog, "email", employee.getEmail(), null);
    addDetail(details, changeLog, "employeeNumber", employee.getEmployeeNumber(), null);
    addDetail(details, changeLog, "status", employee.getStatus().toString(), null);

  }

  private void addDetail(List<ChangeLogDetail> details, ChangeLog changeLog, String property,
      String before,
      String after) {
    ChangeLogDetail detail = new ChangeLogDetail(changeLog, property, before, after);
    details.add(detail);
  }

}
