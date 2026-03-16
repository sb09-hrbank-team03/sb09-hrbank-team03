package com.sb09.hrbank.repository;

import com.sb09.hrbank.dto.request.EmployeeSearchRequest;
import com.sb09.hrbank.dto.response.EmployeeDto;
import org.springframework.data.domain.Slice;

public interface EmployeeRepositoryCustom {

  Slice<EmployeeDto> searchEmployees(EmployeeSearchRequest request);
}
