package com.sb09.hrbank.service;

import com.sb09.hrbank.dto.common.CursorPageResponse;
import com.sb09.hrbank.dto.request.DepartmentSearchRequest;
import com.sb09.hrbank.dto.response.DepartmentDto;
import com.sb09.hrbank.dto.request.DepartmentCreateRequest;
import com.sb09.hrbank.dto.request.DepartmentUpdateRequest;
import java.util.List;

public interface DepartmentService {

  DepartmentDto create(DepartmentCreateRequest request);

  DepartmentDto update(Long id, DepartmentUpdateRequest request);

  DepartmentDto findById(Long id);

  CursorPageResponse<DepartmentDto> searchDepartments(DepartmentSearchRequest request);

  void delete(Long id);


  DepartmentResponse updateDepartment(Long id, DepartmentCreateRequest request);

  void deleteDepartment(Long id);

}
