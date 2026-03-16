package com.sb09.hrbank.service.basic;

import com.sb09.hrbank.dto.request.DepartmentUpdateRequest;
import org.springframework.transaction.annotation.Transactional;
import com.sb09.hrbank.dto.response.DepartmentDto;
import com.sb09.hrbank.dto.request.DepartmentCreateRequest;
import com.sb09.hrbank.entity.Department;
import com.sb09.hrbank.mapper.DepartmentMapper;
import com.sb09.hrbank.repository.DepartmentRepository;
import com.sb09.hrbank.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicDepartmentService implements DepartmentService {

  private final DepartmentRepository departmentRepository;
  private final DepartmentMapper departmentMapper;

  @Override
  public DepartmentDto create(DepartmentCreateRequest request) {

    boolean isDuplicate = departmentRepository.existsByName(request.name());

    if (isDuplicate) {
      throw new IllegalArgumentException("이미 존재하는 부서 이름입니다.");
    }

    Department newDepartment = departmentMapper.toEntity(request);

    Department savedDepartment = departmentRepository.save(newDepartment);

    return departmentMapper.toDto(savedDepartment, 0);
  }

  @Override
  @Transactional
  public DepartmentDto update(Long id, DepartmentUpdateRequest request) {

    Department department = departmentRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부서입니다."));

    if (!department.getName().equals(request.name())) {
      boolean isDuplicate = departmentRepository.existsByName(request.name());
      if (isDuplicate) {
        throw new IllegalArgumentException("이미 존재하는 부서 이름입니다.");
      }
    }

    department.update(request.name(), request.description(), request.establishedDate());
    return departmentMapper.toDto(department, 0);
  }

  @Override
  @Transactional
  public void delete(Long id) {
    if (!departmentRepository.existsById(id)) {
      throw new IllegalArgumentException("존재하지 않는 부서입니다.");
    }
    departmentRepository.deleteById(id);
  }
}
