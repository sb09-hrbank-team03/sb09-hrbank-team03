package com.sb09.hrbank.mapper;

import com.sb09.hrbank.dto.response.EmployeeDto;
import com.sb09.hrbank.entity.Department;
import com.sb09.hrbank.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

  public EmployeeDto toDto(Employee employee) {
    Department department = employee.getDepartment();
    return new EmployeeDto(
        employee.getId(),
        employee.getName(),
        employee.getEmail(),
        employee.getEmployeeNumber(),
        department.getId(),
        department.getName(),
        employee.getPosition(),
        employee.getHireDate(),
        employee.getStatus(),
        employee.getProfileImageId()
    );
  }
}
