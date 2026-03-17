package com.sb09.hrbank.mapper;

import com.sb09.hrbank.dto.response.DepartmentDto;
import com.sb09.hrbank.dto.request.DepartmentCreateRequest;
import com.sb09.hrbank.entity.Department;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

  Department toEntity(DepartmentCreateRequest request);

  DepartmentDto toDto(Department entity, Long employeeCount);

}
