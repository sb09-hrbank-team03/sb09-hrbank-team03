package com.sb09.hrbank.service;

import com.sb09.hrbank.dto.request.DepartmentCreateRequest;
import com.sb09.hrbank.entity.Department;

public interface DepartmentService {

  Department createDepartment(DepartmentCreateRequest request);

}
