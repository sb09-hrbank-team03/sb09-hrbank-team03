package com.sb09.hrbank.dto.request;

import java.time.LocalDate;

public record EmployeeCreateRequest(
    String name,
    String email,
    Long departmentId,
    String position,
    LocalDate hireDate,
    String memo
) {
}
