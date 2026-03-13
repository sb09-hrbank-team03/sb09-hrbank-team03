package com.sb09.hrbank.dto.request;

import com.sb09.hrbank.entity.WorkStatus;
import java.time.LocalDate;

public record EmployeeUpdateRequest (
    String name,
    String email,
    Long departmentId,
    String position,
    LocalDate hireDate,
    WorkStatus status
) {

}
