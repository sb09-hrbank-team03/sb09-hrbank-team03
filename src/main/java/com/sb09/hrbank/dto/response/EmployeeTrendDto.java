package com.sb09.hrbank.dto.response;

import java.time.LocalDate;

public record EmployeeTrendDto(
    LocalDate date,
    Long count,
    Long change,
    double changeRate
) {

}
