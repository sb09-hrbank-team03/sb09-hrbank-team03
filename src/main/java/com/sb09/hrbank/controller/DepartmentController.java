package com.sb09.hrbank.controller;

import com.sb09.hrbank.dto.request.DepartmentCreateRequest;
import com.sb09.hrbank.entity.Department;
import com.sb09.hrbank.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

  private final DepartmentService departmentService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Department createDepartment(
      @Valid @RequestBody DepartmentCreateRequest request
  ) {
    return departmentService.createDepartment(request);
  }
}
