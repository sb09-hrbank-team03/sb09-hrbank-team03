package com.sb09.hrbank.dto.response;

import com.sb09.hrbank.entity.WorkStatus;
import java.time.LocalDate;

public record EmployeeDto(
    Long id,
    String name,
    String email,
    String employeeNumber,
    Long departmentId,
    String departmentName,
    String position,
    LocalDate hireDate,
    WorkStatus status,
    Long profileImageId
) {
}
