package com.sb09.hrbank.service.basic;

import com.sb09.hrbank.dto.common.CursorPageResponse;
import com.sb09.hrbank.dto.request.EmployeeCreateRequest;
import com.sb09.hrbank.dto.request.EmployeeSearchRequest;
import com.sb09.hrbank.dto.request.EmployeeSortField;
import com.sb09.hrbank.dto.request.EmployeeUpdateRequest;
import com.sb09.hrbank.dto.response.EmployeeDistributionDto;
import com.sb09.hrbank.dto.response.EmployeeDto;
import com.sb09.hrbank.dto.response.EmployeeTrendDto;
import com.sb09.hrbank.entity.Department;
import com.sb09.hrbank.entity.Employee;
import com.sb09.hrbank.entity.WorkStatus;
import com.sb09.hrbank.mapper.CursorPageResponseMapper;
import com.sb09.hrbank.mapper.EmployeeMapper;
import com.sb09.hrbank.repository.DepartmentRepository;
import com.sb09.hrbank.repository.EmployeeRepository;
import com.sb09.hrbank.service.ChangeLogService;
import com.sb09.hrbank.service.EmployeeService;
import com.sb09.hrbank.service.FileService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicEmployeeService implements EmployeeService {

  private final DepartmentRepository departmentRepository;
  private final EmployeeRepository employeeRepository;
  private final EmployeeMapper employeeMapper;
  private final FileService fileService;
  private final ChangeLogService changeLogService;
  private final CursorPageResponseMapper cursorPageResponseMapper;

  @Override
  @Transactional
  public EmployeeDto create(EmployeeCreateRequest request, MultipartFile profileImage,
      String clientIp) {
    if (employeeRepository.existsByEmail(request.email())) {
      throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
    }

    Department department = departmentRepository.findById(request.departmentId()).orElseThrow(
        () -> new NoSuchElementException("해당 부서를 찾을 수 없습니다. id=" + request.departmentId()));
    Long profileImageId = getProfileImageId(profileImage);
    Employee savedEmployee = employeeRepository.save(
        createEmployee(request, department, profileImageId));

    changeLogService.createByCreate(savedEmployee, clientIp, request.memo());

    return employeeMapper.toDto(savedEmployee);
  }

  @Override
  @Transactional(readOnly = true)
  public EmployeeDto findById(Long id) {
    Employee employee = employeeRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 직원을 찾을 수 없습니다. id=" + id));
    return employeeMapper.toDto(employee);
  }

  @Override
  @Transactional
  public EmployeeDto update(Long id, EmployeeUpdateRequest request, MultipartFile profileImage,
      String clientIp) {
    Employee employee = employeeRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 직원을 찾을 수 없습니다. id=" + id));
    Long previousProfileImageId = employee.getProfileImageId();
    if (employeeRepository.existsByEmailAndIdNot(request.email(), id)) {
      throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
    }

    Department department = departmentRepository.findById(request.departmentId()).orElseThrow(
        () -> new NoSuchElementException("해당 부서를 찾을 수 없습니다. id=" + request.departmentId()));
    Long profileImageId = getProfileImageId(profileImage);

    changeLogService.createByUpdate(employee, clientIp, request);

    employee.update(request.hireDate(), request.name(), request.email(), request.position(),
        request.status(), department);
    if (profileImageId != null) {
      employee.updateProfileImage(profileImageId);
      if (!Objects.equals(previousProfileImageId, profileImageId)) {
        deleteProfileImageSafely(id, previousProfileImageId);
      }
    }

    return employeeMapper.toDto(employee);
  }

  @Override
  @Transactional
  public void delete(Long id, String clientIp) {
    Employee employee = employeeRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 직원을 찾을 수 없습니다. id=" + id));

    changeLogService.createByDelete(employee, clientIp);

    Long profileImageId = employee.getProfileImageId();
    employeeRepository.delete(employee);
    deleteProfileImageSafely(id, profileImageId);
  }

  private void deleteProfileImageSafely(Long employeeId, Long profileImageId) {
    if (profileImageId == null) {
      return;
    }
    try {
      fileService.delete(profileImageId);
    } catch (NoSuchElementException e) {
      log.warn("프로필 이미지가 이미 삭제됐습니다. employeeId={}, profileImageId={}", employeeId, profileImageId);
    } catch (RuntimeException e) {
      log.error("프로필 이미지 삭제 중 오류가 발생했습니다. employeeId={}, profileImageId={}", employeeId, profileImageId, e);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public CursorPageResponse<EmployeeDto> findAll(EmployeeSearchRequest request) {
    Slice<EmployeeDto> slice = employeeRepository.searchEmployees(request);
    Long totalElements = employeeRepository.count();
    return cursorPageResponseMapper.fromSlice(
        slice,
        dto -> dto,
        dto -> getCursorValue(dto, request),
        EmployeeDto::id,
        totalElements
    );
  }

  private Object getCursorValue(EmployeeDto dto, EmployeeSearchRequest request) {
    EmployeeSortField sortField = request.getSortField();
    if (sortField == null) {
      sortField = EmployeeSortField.name;
    }
    return switch (sortField) {
      case name -> dto.name();
      case employeeNumber -> dto.employeeNumber();
      case hireDate -> dto.hireDate() != null ? dto.hireDate().toString() : null;
    };
  }

  private Employee createEmployee(EmployeeCreateRequest request, Department department,
      Long profileImageId) {
    return Employee.create(request.hireDate(), request.name(), request.email(),
        generateEmployeeNumber(request.hireDate()), request.position(), department, profileImageId);
  }

  private Long getProfileImageId(MultipartFile profileImage) {
    if (profileImage == null || profileImage.isEmpty()) {
      return null;
    }
    return fileService.saveProfileImage(profileImage).getId();
  }

  private int extractSequence(String employeeNumber) {
    String[] parts = employeeNumber.split("-");
    return Integer.parseInt(parts[2]);
  }

  private String generateEmployeeNumber(LocalDate hireDate) {
    String year = String.valueOf(hireDate.getYear());
    String prefix = "EMP-" + year + "-";

    int nextSequence = employeeRepository.findTopByEmployeeNumberStartingWithOrderByEmployeeNumberDesc(
        prefix).map(employee -> extractSequence(employee.getEmployeeNumber()) + 1).orElse(1);

    return prefix + String.format("%03d", nextSequence);
  }

  @Override
  @Transactional(readOnly = true)
  public List<EmployeeTrendDto> trend(LocalDate from, LocalDate to, String unit) {
    LocalDate now = LocalDate.now();
    LocalDate toDate = to != null ? to : now;
    LocalDate fromDate = from != null ? from : convert(now, unit, -12);
    List<EmployeeTrendDto> result = new ArrayList<>();
    LocalDate flag = fromDate;
    Long prevCount = employeeRepository.countByHireDateLessThanEqual(convert(fromDate, unit, -1));
    while (!flag.isAfter(toDate)) {
      LocalDate nextFlag = convert(flag, unit, 1);
      Long count = employeeRepository.countByHireDateLessThanEqual(flag);
      Long change = count - prevCount;
      double changeRate = prevCount > 0 ? (double) change / prevCount * 100.0 : 0.0;
      String changeString = String.format("%.1f", changeRate);
      EmployeeTrendDto dto = new EmployeeTrendDto(flag, count, change, changeString);
      result.add(dto);
      prevCount = count;
      flag = nextFlag;
    }
    return result;
  }

  private LocalDate convert(LocalDate date, String unit, int amount) {
    return switch (unit) {
      case "day"     -> date.plusDays(amount);
      case "week"    -> date.plusWeeks(amount);
      case "month"   -> date.plusMonths(amount);
      case "quarter" -> date.plusMonths(amount*3);
      case "year"    -> date.plusYears(amount);
      default        -> date.plusMonths(amount);
    };
  }


  @Override
  @Transactional(readOnly = true)
  public List<EmployeeDistributionDto> distribution(String groupBy, String status) {
    List<EmployeeDistributionDto> dos = new ArrayList<>();
    WorkStatus workStatus = WorkStatus.valueOf(status);
    List<Object[]> countWithGroup =
        groupBy.equals("position") ? employeeRepository.countGroupByPosition(workStatus)
            : employeeRepository.countGroupByDepartment(workStatus);
    Long total = employeeRepository.countByStatus(workStatus);
    for (Object[] o : countWithGroup) {
      double percentage = total != 0 ? ((Number) o[1]).doubleValue() / total * 100.0 : 0.0;
      String percentString = String.format("%.1f", percentage);
      EmployeeDistributionDto dto = new EmployeeDistributionDto((String) o[0],
          ((Number) o[1]).longValue(), percentString);
      dos.add(dto);
    }
    return dos;
  }

  @Override
  @Transactional(readOnly = true)
  public Long count(String status, LocalDate fromDate, LocalDate to) {
    Long count;
    LocalDate now = LocalDate.now();
    LocalDate toDate = to != null ? to : now;
    if (status != null && !status.isBlank()) {
      WorkStatus workStatus = WorkStatus.valueOf(status);
      if (fromDate != null) {
        count = employeeRepository.countByHireDateBetweenAndStatus(fromDate, toDate, workStatus);
      } else {
        count = employeeRepository.countByStatus(workStatus);
      }
    } else {
      if (fromDate != null) {
        count = employeeRepository.countByHireDateBetween(fromDate, toDate);
      } else {
        count = employeeRepository.count();
      }
    }
    return count;
  }
}