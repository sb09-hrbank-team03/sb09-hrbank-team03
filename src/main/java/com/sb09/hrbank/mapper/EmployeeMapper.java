package com.sb09.hrbank.mapper;

import com.sb09.hrbank.dto.response.EmployeeDto;
import com.sb09.hrbank.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

  @Mapping(target = "departmentId", source = "department.id")
  @Mapping(target = "departmentName", source = "department.name")
  EmployeeDto toDto(Employee employee);
}
