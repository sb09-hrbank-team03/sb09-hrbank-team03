package com.sb09.hrbank.repository;

import com.sb09.hrbank.entity.Employee;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
  boolean existsByEmail(String email);

  boolean existsByEmployeeNumber(String employeeNumber);

  Optional<Employee> findTopByEmployeeNumberStartingWithOrderByEmployeeNumberDesc(String prefix);
}
