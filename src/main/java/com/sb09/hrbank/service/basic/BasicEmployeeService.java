package com.sb09.hrbank.service.basic;

import com.sb09.hrbank.dto.request.EmployeeCreateRequest;
import com.sb09.hrbank.dto.request.EmployeeUpdateRequest;
import com.sb09.hrbank.dto.response.EmployeeDto;
import com.sb09.hrbank.entity.Department;
import com.sb09.hrbank.entity.Employee;
import com.sb09.hrbank.mapper.EmployeeMapper;
import com.sb09.hrbank.repository.DepartmentRepository;
import com.sb09.hrbank.repository.EmployeeRepository;
import com.sb09.hrbank.service.EmployeeService;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BasicEmployeeService implements EmployeeService {

  private final DepartmentRepository departmentRepository;
  private final EmployeeRepository employeeRepository;
  private final EmployeeMapper employeeMapper;

  @Override
  @Transactional
  public EmployeeDto create(EmployeeCreateRequest request, MultipartFile profileImage) {
    if (employeeRepository.existsByEmail(request.email())) {
      throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
    }

    Department department = departmentRepository.findById(request.departmentId())
        .orElseThrow(() -> new NoSuchElementException("해당 부서를 찾을 수 없습니다. id=" + request.departmentId()));
    Long profileImageId = getProfileImageId(profileImage);
    if (profileImage != null && !profileImage.isEmpty() && profileImageId == null) {
      throw new UnsupportedOperationException("프로필 이미지 업로드 기능이 아직 구현되지 않았습니다.");
    }

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

    if (employeeRepository.existsByEmailAndIdNot(request.email(), id)) {
      throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
    }

    Department department = departmentRepository.findById(request.departmentId())
        .orElseThrow(() -> new NoSuchElementException("해당 부서를 찾을 수 없습니다. id=" + request.departmentId()));
    Long profileImageId = getProfileImageId(profileImage);
    if (profileImage != null && !profileImage.isEmpty() && profileImageId == null) {
      throw new UnsupportedOperationException("프로필 이미지 업로드 기능이 아직 구현되지 않았습니다.");
    }

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
}