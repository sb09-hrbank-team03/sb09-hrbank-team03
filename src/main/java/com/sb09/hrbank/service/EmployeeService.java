package com.sb09.hrbank.service;

import com.sb09.hrbank.dto.common.CursorPageResponse;
import com.sb09.hrbank.dto.request.EmployeeCreateRequest;
import com.sb09.hrbank.dto.request.EmployeeSearchRequest;
import com.sb09.hrbank.dto.request.EmployeeUpdateRequest;
import com.sb09.hrbank.dto.response.EmployeeDistributionDto;
import com.sb09.hrbank.dto.response.EmployeeDto;
import com.sb09.hrbank.dto.response.EmployeeTrendDto;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface EmployeeService {

  EmployeeDto create(EmployeeCreateRequest request, MultipartFile profileImage, String clientIp);

  EmployeeDto update(Long id, EmployeeUpdateRequest request, MultipartFile profileImage, String clientIp);

  EmployeeDto findById(Long id);

  CursorPageResponse<EmployeeDto> findAll(EmployeeSearchRequest request);

  void delete(Long id, String clientIp);

  List<EmployeeTrendDto> trend(LocalDate from, LocalDate to, String unit);

  List<EmployeeDistributionDto> distribution(String groupBy, String status);

  Long count(String status, LocalDate fromDate, LocalDate toDate);
}
