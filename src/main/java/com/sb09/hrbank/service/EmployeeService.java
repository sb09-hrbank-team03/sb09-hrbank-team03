package com.sb09.hrbank.service;

import com.sb09.hrbank.dto.request.EmployeeCreateRequest;
import com.sb09.hrbank.dto.response.EmployeeDto;

public interface EmployeeService {

  EmployeeDto create(EmployeeCreateRequest request);

  EmployeeDto getById(Long id);
}
