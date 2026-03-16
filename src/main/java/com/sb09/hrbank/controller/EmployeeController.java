package com.sb09.hrbank.controller;

import com.sb09.hrbank.dto.request.EmployeeCreateRequest;
import com.sb09.hrbank.dto.request.EmployeeUpdateRequest;
import com.sb09.hrbank.dto.response.EmployeeDto;
import com.sb09.hrbank.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

  private final EmployeeService employeeService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public EmployeeDto create(
      @RequestHeader(value = "X-Forwarded-For", required = false) String ip,
      HttpServletRequest servletRequest,
      @RequestPart("request") EmployeeCreateRequest request,
      @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
  ) {
    String clientIp;
    if (ip != null && !ip.isBlank()) {
      clientIp = ip.split(",")[0].trim();
    } else {
      clientIp = servletRequest.getRemoteAddr();
    }
    return employeeService.create(request, profileImage, clientIp);
  }

  @GetMapping("/{id}")
  public EmployeeDto findById(@PathVariable Long id) {
    return employeeService.findById(id);
  }

  @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public EmployeeDto update(
      @RequestHeader(value = "X-Forwarded-For", required = false) String ip,
      HttpServletRequest servletRequest,
      @PathVariable Long id,
      @RequestPart("request") EmployeeUpdateRequest request,
      @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
  ) {
    String clientIp;
    if (ip != null && !ip.isBlank()) {
      clientIp = ip.split(",")[0].trim();
    } else {
      clientIp = servletRequest.getRemoteAddr();
    }
    return employeeService.update(id, request, profileImage, clientIp);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(
      @RequestHeader(value = "X-Forwarded-For", required = false) String ip,
      HttpServletRequest servletRequest, @PathVariable Long id
  ) {
    String clientIp;
    if (ip != null && !ip.isBlank()) {
      clientIp = ip.split(",")[0].trim();
    } else {
      clientIp = servletRequest.getRemoteAddr();
    }
    employeeService.delete(id, clientIp);
  }
}
