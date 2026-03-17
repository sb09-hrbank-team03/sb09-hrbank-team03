package com.sb09.hrbank.service;

import com.sb09.hrbank.dto.common.CursorPageResponse;
import com.sb09.hrbank.dto.request.DepartmentSearchRequest;
import com.sb09.hrbank.dto.response.DepartmentDto;
import com.sb09.hrbank.dto.request.DepartmentCreateRequest;
import com.sb09.hrbank.dto.request.DepartmentUpdateRequest;

public interface DepartmentService {

  DepartmentDto create(DepartmentCreateRequest request);

  DepartmentDto update(Long id, DepartmentUpdateRequest request);

  DepartmentDto findById(Long id);

  CursorPageResponse<DepartmentDto> searchDepartments(DepartmentSearchRequest request);

  void delete(Long id);


}
