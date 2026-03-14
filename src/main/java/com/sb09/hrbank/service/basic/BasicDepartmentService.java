package com.sb09.hrbank.service.basic;


import com.sb09.hrbank.dto.request.DepartmentCreateRequest;
import com.sb09.hrbank.entity.Department;
import com.sb09.hrbank.repository.DepartmentRepository;
import com.sb09.hrbank.service.DepartmentService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicDepartmentService implements DepartmentService {

  private final DepartmentRepository departmentRepository;

  @Override
  public void createDepartment(DepartmentCreateRequest request) {

    boolean isDuplicate = departmentRepository.existsByName(request.name());

    if (isDuplicate) {
      throw new IllegalArgumentException("이미 존재하는 부서 이름입니다.");
    }

    Department newDepartment = Department.builder()
        .name(request.name())
        .description(request.description())
        .establishedDate(request.establishedDate())
        .build();

    departmentRepository.save(newDepartment);
  }
}
