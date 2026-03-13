package com.sb09.hrbank.service.basic;


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

  public void createDepartment(String name, String description, LocalDate establishedDate) {

    boolean isDuplicate = departmentRepository.existsByName(name);

    if (isDuplicate) {
      throw new IllegalArgumentException("이미 존재하는 부서 이름입니다.");
    }

    Department newDepartment = Department.builder()
        .name(name)
        .description(description)
        .establishedDate(establishedDate)
        .build();

    departmentRepository.save(newDepartment);
  }
}
