package com.sb09.hrbank.service.basic;

import com.sb09.hrbank.dto.request.EmployeeCreateRequest;
import com.sb09.hrbank.dto.response.EmployeeDto;
import com.sb09.hrbank.entity.Employee;
import com.sb09.hrbank.repository.DepartmentRepository;
import com.sb09.hrbank.repository.EmployeeQueryRepository;
import com.sb09.hrbank.repository.EmployeeRepository;
import com.sb09.hrbank.service.EmployeeService;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicEmployeeService implements EmployeeService {

  private static final int MAX_EMPLOYEE_NUMBER_RETRIES = 5;

  private final DepartmentRepository departmentRepository;
  private final EmployeeQueryRepository employeeQueryRepository;
  private final EmployeeRepository employeeRepository;
  private final EmployeeNumberGenerator employeeNumberGenerator;

  @Override
  public EmployeeDto create(EmployeeCreateRequest request) {
    validateEmailNotDuplicated(request.email());
    validateDepartmentExists(request.departmentId());

    for (int attempt = 0; attempt < MAX_EMPLOYEE_NUMBER_RETRIES; attempt++) {
      try {
        Employee savedEmployee = employeeRepository.save(createEmployee(request));
        return findEmployeeDtoOrThrow(savedEmployee.getId());
      } catch (DataIntegrityViolationException e) {
        validateEmailNotDuplicated(request.email());

        if (attempt == MAX_EMPLOYEE_NUMBER_RETRIES - 1) {
          throw new IllegalStateException("사번 생성에 실패했습니다. 잠시 후 다시 시도해주세요.");
        }
      }
    }

    throw new IllegalStateException("사번 생성에 실패했습니다. 잠시 후 다시 시도해주세요.");
  }

  @Override
  public EmployeeDto getById(Long id) {
    return findEmployeeDtoOrThrow(id);
  }

  private void validateEmailNotDuplicated(String email) {
    if (employeeRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
    }
  }

  private Employee createEmployee(EmployeeCreateRequest request) {
    return Employee.create(
        request.hireDate(),
        request.name(),
        request.email(),
        employeeNumberGenerator.generate(request.hireDate()),
        request.position(),
        request.departmentId(),
        null
    );
  }

  private EmployeeDto findEmployeeDtoOrThrow(Long id) {
    return employeeQueryRepository.findEmployeeDtoById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 직원을 찾을 수 없습니다. id=" + id));
  }

  private void validateDepartmentExists(Long departmentId) {
    if (!departmentRepository.existsById(departmentId)) {
      throw new NoSuchElementException("해당 부서를 찾을 수 없습니다. id=" + departmentId);
    }
  }
}