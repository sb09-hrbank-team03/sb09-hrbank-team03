package com.sb09.hrbank.repository;

import com.sb09.hrbank.entity.Employee;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, EmployeeRepositoryCustom {
  boolean existsByEmail(String email);

  boolean existsByEmailAndIdNot(String email, Long id);

  Optional<Employee> findTopByEmployeeNumberStartingWithOrderByEmployeeNumberDesc(String prefix);

  boolean existsByUpdatedAtAfter(Instant time);

  List<Employee> findTop100ByIdGreaterThanOrderByIdAsc(Long id);

}
