package com.sb09.hrbank.service;

import com.sb09.hrbank.dto.common.DepartmentResponse;
import com.sb09.hrbank.dto.request.DepartmentCreateRequest;
import com.sb09.hrbank.dto.request.DepartmentUpdateRequest;
import com.sb09.hrbank.entity.Department;

public interface DepartmentService {

  DepartmentResponse create(DepartmentCreateRequest request);

  DepartmentResponse update(Long id, DepartmentUpdateRequest request);

  void delete(Long id);

}
