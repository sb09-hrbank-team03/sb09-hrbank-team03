package com.sb09.hrbank.controller;

import com.sb09.hrbank.dto.request.EmployeeCreateRequest;
import com.sb09.hrbank.dto.response.EmployeeDto;
import com.sb09.hrbank.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

  private final EmployeeService employeeService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public EmployeeDto create(@RequestBody EmployeeCreateRequest request) {
    return employeeService.create(request);
  }

  @GetMapping("/{id}")
  public EmployeeDto getById(@PathVariable Long id) {
    return employeeService.getById(id);
  }
}
