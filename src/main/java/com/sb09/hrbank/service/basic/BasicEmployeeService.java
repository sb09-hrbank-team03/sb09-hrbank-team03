package com.sb09.hrbank.service.basic;

import com.sb09.hrbank.dto.common.CursorPageResponse;
import com.sb09.hrbank.dto.request.EmployeeCreateRequest;
import com.sb09.hrbank.dto.request.EmployeeSearchRequest;
import com.sb09.hrbank.dto.request.EmployeeSortField;
import com.sb09.hrbank.dto.request.EmployeeUpdateRequest;
import com.sb09.hrbank.dto.response.EmployeeDto;
import com.sb09.hrbank.entity.Department;
import com.sb09.hrbank.entity.Employee;
import com.sb09.hrbank.mapper.CursorPageResponseMapper;
import com.sb09.hrbank.mapper.EmployeeMapper;
import com.sb09.hrbank.repository.DepartmentRepository;
import com.sb09.hrbank.repository.EmployeeRepository;
import com.sb09.hrbank.service.EmployeeService;
import com.sb09.hrbank.service.FileService;
import java.time.LocalDate;
import java.util.Objects;
import java.util.NoSuchElementException;
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
  private final CursorPageResponseMapper cursorPageResponseMapper;

  @Override
  @Transactional
  public EmployeeDto create(EmployeeCreateRequest request, MultipartFile profileImage) {
    if (employeeRepository.existsByEmail(request.email())) {
      throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
    }
    Department department = departmentRepository
        .findById(request.departmentId())
        .orElseThrow(() -> new NoSuchElementException("해당 부서를 찾을 수 없습니다. id=" + request.departmentId()));
    Long profileImageId = getProfileImageId(profileImage);
    Employee savedEmployee = employeeRepository.save(createEmployee(request, department, profileImageId));
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
  public EmployeeDto update(Long id, EmployeeUpdateRequest request, MultipartFile profileImage) {
    Employee employee = employeeRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 직원을 찾을 수 없습니다. id=" + id));
    Long previousProfileImageId = employee.getProfileImageId();

    if (employeeRepository.existsByEmailAndIdNot(request.email(), id)) {
      throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
    }
    Department department = departmentRepository
        .findById(request.departmentId())
        .orElseThrow(() -> new NoSuchElementException("해당 부서를 찾을 수 없습니다. id=" + request.departmentId()));
    Long profileImageId = getProfileImageId(profileImage);
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
      if (!Objects.equals(previousProfileImageId, profileImageId)) {
        deleteProfileImageSafely(id, previousProfileImageId);
      }
    }
    return employeeMapper.toDto(employee);
  }

  @Override
  @Transactional
  public void delete(Long id) {
    Employee employee = employeeRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 직원을 찾을 수 없습니다. id=" + id));
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
    return cursorPageResponseMapper.fromSlice(
        slice,
        dto -> dto,
        dto -> getCursorValue(dto, request),
        EmployeeDto::id
    );
  }

  private Object getCursorValue(EmployeeDto dto, EmployeeSearchRequest request) {
    EmployeeSortField sortBy = request.getSortBy();
    if (sortBy == null) {
      sortBy = EmployeeSortField.hireDate;
    }
    return switch (sortBy) {
      case name -> dto.name();
      case employeeNumber -> dto.employeeNumber();
      case hireDate -> dto.hireDate() != null ? dto.hireDate().toString() : null;
    };
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
    return fileService.saveProfileImage(profileImage).getId();
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
}
