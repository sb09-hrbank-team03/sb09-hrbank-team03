package com.sb09.hrbank.dto.common;

import java.time.LocalDate;

public record DepartmentResponse(

    Long id,
    String name,
    String description,
    LocalDate establishedDate
) {

}
