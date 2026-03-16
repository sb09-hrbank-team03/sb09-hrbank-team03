package com.sb09.hrbank.service.basic;

import com.sb09.hrbank.dto.request.EmployeeCreateRequest;
import com.sb09.hrbank.dto.request.EmployeeUpdateRequest;
import com.sb09.hrbank.dto.response.EmployeeDistributionDto;
import com.sb09.hrbank.dto.response.EmployeeDto;
import com.sb09.hrbank.dto.response.EmployeeTrendDto;
import com.sb09.hrbank.entity.Department;
import com.sb09.hrbank.entity.Employee;
import com.sb09.hrbank.entity.WorkStatus;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  @Override
  @Transactional
  public EmployeeDto create(EmployeeCreateRequest request, MultipartFile profileImage, String clientIp) {
    if (employeeRepository.existsByEmail(request.email())) {
      throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
    }

    Department department = departmentRepository
        .findById(request.departmentId())
        .orElseThrow(() -> new NoSuchElementException("해당 부서를 찾을 수 없습니다. id=" + request.departmentId()));
    Long profileImageId = getProfileImageId(profileImage);
    if (profileImage != null && !profileImage.isEmpty() && profileImageId == null) {
      throw new UnsupportedOperationException("프로필 이미지 업로드 기능이 아직 구현되지 않았습니다.");
    }

    Employee savedEmployee = employeeRepository.save(createEmployee(request, department, profileImageId));

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
  public EmployeeDto update(Long id, EmployeeUpdateRequest request, MultipartFile profileImage, String clientIp) {
    Employee employee = employeeRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 직원을 찾을 수 없습니다. id=" + id));

    if (employeeRepository.existsByEmailAndIdNot(request.email(), id)) {
      throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
    }

    Department department = departmentRepository
        .findById(request.departmentId())
        .orElseThrow(() -> new NoSuchElementException("해당 부서를 찾을 수 없습니다. id=" + request.departmentId()));
    Long profileImageId = getProfileImageId(profileImage);
    if (profileImage != null && !profileImage.isEmpty() && profileImageId == null) {
      throw new UnsupportedOperationException("프로필 이미지 업로드 기능이 아직 구현되지 않았습니다.");
    }

    changeLogService.createByUpdate(employee,clientIp,request);

    employee.update(
        request.hireDate(),
        request.name(),
        request.email(),
        request.position(),
        request.status(),
        department
    );
    if (profileImageId != null) {
      employee.updateProfileImage(profileImageId);
    }

    return employeeMapper.toDto(employee);
  }

  @Override
  @Transactional
  public void delete(Long id, String clientIp) {
    Employee employee = employeeRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 직원을 찾을 수 없습니다. id=" + id));

    changeLogService.createByDelete(employee,clientIp);

    Long profileImageId = employee.getProfileImageId();
    employeeRepository.delete(employee);

    if (profileImageId != null) {
      try {
        fileService.delete(profileImageId);
      } catch (NoSuchElementException e) {
        log.warn("프로필 이미지가 이미 삭제되어 있습니다. employeeId={}, profileImageId={}", id, profileImageId);
      }
    }
  }

  private Employee createEmployee(EmployeeCreateRequest request, Department department, Long profileImageId) {
    return Employee.create(
        request.hireDate(),
        request.name(),
        request.email(),
        generateEmployeeNumber(request.hireDate()),
        request.position(),
        department,
        profileImageId
    );
  }

  private Long getProfileImageId(MultipartFile profileImage) {
    if (profileImage == null || profileImage.isEmpty()) {
      return null;
    }
    return null;
  }

  private int extractSequence(String employeeNumber) {
    String[] parts = employeeNumber.split("-");
    return Integer.parseInt(parts[2]);
  }

  private String generateEmployeeNumber(LocalDate hireDate) {
    String year = String.valueOf(hireDate.getYear());
    String prefix = "EMP-" + year + "-";

    int nextSequence = employeeRepository
        .findTopByEmployeeNumberStartingWithOrderByEmployeeNumberDesc(prefix)
        .map(employee -> extractSequence(employee.getEmployeeNumber()) + 1)
        .orElse(1);

    return prefix + String.format("%03d", nextSequence);
  }

  @Override
  @Transactional(readOnly = true)
  public List<EmployeeTrendDto> trend(LocalDate from, LocalDate to, String unit) {
    LocalDate now = LocalDate.now();
    LocalDate toDate = to != null ? to : now;
    LocalDate fromDate = from != null ? from : switch (unit) {
      case "day"     -> now.minusDays(12);
      case "week"    -> now.minusWeeks(12);
      case "month"   -> now.minusMonths(12);
      case "quarter" -> now.minusMonths(36);
      case "year"    -> now.minusYears(12);
      default        -> now.minusMonths(12);
    };
    List<EmployeeTrendDto> result = new ArrayList<>();
    LocalDate flag = fromDate;
    Long prevCount = null;
    while(!flag.isAfter(toDate)){
      LocalDate nextFlag = switch (unit){
        case "day"     -> flag.plusDays(1);
        case "week"    -> flag.plusWeeks(1);
        case "month"   -> flag.plusMonths(1);
        case "quarter" -> flag.plusMonths(3);
        case "year"    -> flag.plusYears(1);
        default        -> flag.plusMonths(1);
      };
      Long count = employeeRepository.countByHireDateLessThanEqual(nextFlag);
      Long change = prevCount != null ? count - prevCount : 0;
      double changeRate = prevCount != null && prevCount > 0 ?
          Math.round((double) change / prevCount * 1000.0) / 10.0 : 0.0;
      EmployeeTrendDto dto = new EmployeeTrendDto(flag, count, change, changeRate);
      result.add(dto);
      prevCount = count;
      flag = nextFlag;
    }
    return result;
  }

  @Override
  @Transactional(readOnly = true)
  public List<EmployeeDistributionDto> distribution(String groupBy, String status) {
    List<EmployeeDistributionDto> dtos = new ArrayList<>();
    WorkStatus workStatus = WorkStatus.valueOf(status);
    List<Object[]> countWithGroup = groupBy.equals("position")
        ? employeeRepository.countGroupByPosition(workStatus)
        : employeeRepository.countGroupByDepartment(workStatus);
    Long total = employeeRepository.countByStatus(workStatus);
    for(Object[] o : countWithGroup){
      // math.round가 결과가 정수로 나와서 소수점 표현 때문에 100 대신 1000 곱하고 10으로 나눔.
      double percentage = total != 0 ? Math.round((double) o[1] / total * 1000.0) / 10.0 : 0.0;
      EmployeeDistributionDto dto = new EmployeeDistributionDto(
          (String) o[0],
          (Long) o[1],
          percentage
      );
      dtos.add(dto);
    }
    return dtos;
  }

  @Override
  @Transactional(readOnly = true)
  public Long count(String status, LocalDate fromDate, LocalDate to) {
    Long count;
    LocalDate now = LocalDate.now();
    LocalDate toDate = to != null ? to : now;
    if(status != null){
      WorkStatus workStatus = WorkStatus.valueOf(status);
      if(fromDate != null){
        count = employeeRepository.countByHireDateBetweenAndStatus(fromDate, toDate, workStatus);
      }
      else{
        count = employeeRepository.countByStatus(workStatus);
      }
    }
    else{
      if(fromDate != null){
        count = employeeRepository.countByHireDateBetween(fromDate, toDate);
      }
      else{
        count = employeeRepository.count();
      }
    }
    return count;
  }
}