package com.sb09.hrbank.controller;

import com.sb09.hrbank.dto.response.DepartmentDto;
import com.sb09.hrbank.dto.request.DepartmentCreateRequest;
import com.sb09.hrbank.dto.request.DepartmentUpdateRequest;
import com.sb09.hrbank.service.DepartmentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

  private final DepartmentService departmentService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public DepartmentDto create(
      @Valid @RequestBody DepartmentCreateRequest request
  ) {
    return departmentService.create(request);
  }

  @PatchMapping("/{id}")
  public DepartmentDto update(
      @PathVariable Long id,
      @Valid @RequestBody DepartmentUpdateRequest request
  ) {
    return departmentService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    departmentService.delete(id);
  }

  @GetMapping
  public List<DepartmentDto> findAll(
      @RequestParam(required = false) String keyword
  ) {
    return departmentService.findAll(keyword);
  }

  @GetMapping("/{id}")
  public DepartmentDto findById(@PathVariable Long id) {
    return departmentService.findById(id);
  }
}
