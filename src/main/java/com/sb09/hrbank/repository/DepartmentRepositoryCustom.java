package com.sb09.hrbank.repository;

import com.sb09.hrbank.dto.request.DepartmentSearchRequest;
import com.sb09.hrbank.entity.Department;
import org.springframework.data.domain.Slice;

public interface DepartmentRepositoryCustom {

  Slice<Department> searchDepartments(DepartmentSearchRequest request);

}
