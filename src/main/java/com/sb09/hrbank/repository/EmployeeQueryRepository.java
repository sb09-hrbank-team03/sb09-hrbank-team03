package com.sb09.hrbank.repository;

import com.sb09.hrbank.dto.response.EmployeeDto;
import com.sb09.hrbank.entity.Employee;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface EmployeeQueryRepository extends Repository<Employee, Long> {

  @Query("""
      select new com.sb09.hrbank.dto.response.EmployeeDto(
        e.id,
        e.name,
        e.email,
        e.employeeNumber,
        e.departmentId,
        d.name,
        e.position,
        e.hireDate,
        e.status,
        e.profileImageId
      )
      from Employee e
      left join Department d on d.id = e.departmentId
      where e.id = :id
      """)
  Optional<EmployeeDto> findEmployeeDtoById(@Param("id") Long id);
}

