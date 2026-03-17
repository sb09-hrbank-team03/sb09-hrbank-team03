package com.sb09.hrbank.repository;

import com.sb09.hrbank.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long>,
    DepartmentRepositoryCustom {

  boolean existsByName(String name);

  boolean existsByNameAndIdNot(String name, Long id);

}
