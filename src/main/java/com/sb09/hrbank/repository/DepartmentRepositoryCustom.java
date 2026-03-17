package com.sb09.hrbank.repository;

import com.sb09.hrbank.dto.request.DepartmentSearchRequest;
import com.sb09.hrbank.dto.response.DepartmentDto;
import org.springframework.data.domain.Slice;

public interface DepartmentRepositoryCustom {

  Slice<DepartmentDto> searchDepartments(DepartmentSearchRequest request);

}
