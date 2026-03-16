package com.sb09.hrbank.service;

import com.sb09.hrbank.dto.common.DepartmentResponse;
import com.sb09.hrbank.dto.request.DepartmentCreateRequest;
import com.sb09.hrbank.entity.Department;

public interface DepartmentService {

  DepartmentResponse createDepartment(DepartmentCreateRequest request);

}
