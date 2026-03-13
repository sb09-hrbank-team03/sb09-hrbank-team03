package com.sb09.hrbank.repository;

import com.sb09.hrbank.entity.Department;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

  boolean existsByName(String name);

  List<Department> findByNameContainingOrDescriptionContaining(String name, String description);
}
