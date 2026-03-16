package com.sb09.hrbank.service;

import com.sb09.hrbank.dto.request.EmployeeCreateRequest;
import com.sb09.hrbank.dto.request.EmployeeUpdateRequest;
import com.sb09.hrbank.dto.response.EmployeeDto;
import org.springframework.web.multipart.MultipartFile;

public interface EmployeeService {

  EmployeeDto create(EmployeeCreateRequest request, MultipartFile profileImage, String clientIp);

  EmployeeDto update(Long id, EmployeeUpdateRequest request, MultipartFile profileImage, String clientIp);

  EmployeeDto findById(Long id);


  void delete(Long id, String clientIp);
}
