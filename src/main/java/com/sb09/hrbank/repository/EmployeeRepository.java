package com.sb09.hrbank.repository;

import com.sb09.hrbank.entity.Employee;
import com.sb09.hrbank.entity.WorkStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, EmployeeRepositoryCustom {
  boolean existsByEmail(String email);

  boolean existsByEmailAndIdNot(String email, Long id);

  Optional<Employee> findTopByEmployeeNumberStartingWithOrderByEmployeeNumberDesc(String prefix);

  boolean existsByUpdatedAtAfter(Instant time);

  List<Employee> findByIdGreaterThanOrderByIdAsc(Long id, Pageable pageable);

  Long countByHireDateLessThanEqual(LocalDate date);

  @Query("SELECT COUNT(e) FROM Employee e WHERE e.status = :status")
  Long countByStatus(@Param("status") WorkStatus status);

  @Query("SELECT e.department.name, COUNT(e) FROM Employee e WHERE e.status = :status GROUP BY e.department.name")
  List<Object[]> countGroupByDepartment(@Param("status") WorkStatus status);

  @Query("SELECT e.position, COUNT(e) FROM Employee e WHERE e.status = :status GROUP BY e.position")
  List<Object[]> countGroupByPosition(@Param("status") WorkStatus status);

  Long countByHireDateBetween(LocalDate from, LocalDate to);
  
  Long countByHireDateBetweenAndStatus(LocalDate from, LocalDate to, WorkStatus status);

  boolean existsByDepartmentId(Long departmentId);

  long countByDepartmentId(Long departmentId);

}
