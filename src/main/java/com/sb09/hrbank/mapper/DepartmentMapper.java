package com.sb09.hrbank.mapper;

import com.sb09.hrbank.dto.common.DepartmentResponse;
import com.sb09.hrbank.dto.request.DepartmentCreateRequest;
import com.sb09.hrbank.entity.Department;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

  Department toEntity(DepartmentCreateRequest request);

  DepartmentResponse toDto(Department entity);
}
