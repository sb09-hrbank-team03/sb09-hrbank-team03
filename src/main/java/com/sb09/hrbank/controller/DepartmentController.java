package com.sb09.hrbank.controller;

import com.sb09.hrbank.dto.common.DepartmentResponse;
import com.sb09.hrbank.dto.request.DepartmentCreateRequest;
import com.sb09.hrbank.dto.request.DepartmentUpdateRequest;
import com.sb09.hrbank.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
  public DepartmentResponse createDepartment(
      @Valid @RequestBody DepartmentCreateRequest request
  ) {
    return departmentService.createDepartment(request);
  }

  @PutMapping("/{id}")
  public DepartmentResponse updateDepartment(
      @PathVariable Long id,
      @Valid @RequestBody DepartmentUpdateRequest request
  ) {
    return departmentService.updateDepartment(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteDepartment(@PathVariable Long id) {
    departmentService.deleteDepartment(id);
  }
}
