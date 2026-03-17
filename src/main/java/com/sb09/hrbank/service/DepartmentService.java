package com.sb09.hrbank.service;

import com.sb09.hrbank.dto.response.DepartmentDto;
import com.sb09.hrbank.dto.request.DepartmentCreateRequest;
import com.sb09.hrbank.dto.request.DepartmentUpdateRequest;
import java.util.List;

public interface DepartmentService {

  DepartmentDto create(DepartmentCreateRequest request);

  DepartmentDto update(Long id, DepartmentUpdateRequest request);

  DepartmentDto findById(Long id);

  List<DepartmentDto> findAll(String keyword);

  void delete(Long id);


}
