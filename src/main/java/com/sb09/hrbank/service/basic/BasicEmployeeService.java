package com.sb09.hrbank.service.basic;

import com.sb09.hrbank.dto.request.EmployeeCreateRequest;
import com.sb09.hrbank.dto.response.EmployeeDto;
import com.sb09.hrbank.entity.Employee;
import com.sb09.hrbank.repository.EmployeeRepository;
import com.sb09.hrbank.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicEmployeeService implements EmployeeService {

  private final EmployeeRepository employeeRepository;

  @Override
  public EmployeeDto create(EmployeeCreateRequest request) {
    if (employeeRepository.existsByEmail(request.email())) {
      throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
    }

    Employee employee = Employee.create(
        request.hireDate(),
        request.name(),
        request.email(),
        generateEmployeeNumber(),
        request.position(),
        request.departmentId(),
        null
    );

    Employee savedEmployee = employeeRepository.save(employee);

    return new EmployeeDto(
        savedEmployee.getId(),
        savedEmployee.getName(),
        savedEmployee.getEmail(),
        savedEmployee.getEmployeeNumber(),
        savedEmployee.getDepartmentId(),
        null,
        savedEmployee.getPosition(),
        savedEmployee.getHireDate(),
        savedEmployee.getStatus(),
        savedEmployee.getProfileImageId()
    );
  }

  private String generateEmployeeNumber() {
    long count = employeeRepository.count() + 1;
    return "EMP-" + String.format("%06d", count);
  }
}