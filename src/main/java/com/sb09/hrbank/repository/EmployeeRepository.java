package com.sb09.hrbank.repository;

import com.sb09.hrbank.dto.response.EmployeeDto;
import com.sb09.hrbank.entity.Employee;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
  boolean existsByEmail(String email);

  boolean existsByEmailAndIdNot(String email, Long id);

  boolean existsByEmployeeNumber(String employeeNumber);

  Optional<Employee> findTopByEmployeeNumberStartingWithOrderByEmployeeNumberDesc(String prefix);

  @Query("""
      select new com.sb09.hrbank.dto.response.EmployeeDto(
        e.id,
        e.name,
        e.email,
        e.employeeNumber,
        d.id,
        d.name,
        e.position,
        e.hireDate,
        e.status,
        e.profileImageId
      )
      from Employee e
      left join e.department d
      where e.id = :id
      """)

  Optional<EmployeeDto> findEmployeeDtoById(@Param("id") Long id);

  Slice<Employee> findAllBy(Pageable pageable);

  boolean existsByUpdatedAtAfter(Instant time);

  List<Employee> findTop100ByIdGreaterThanOrderByIdAsc(Long id);

}
