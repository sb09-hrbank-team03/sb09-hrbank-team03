package com.sb09.hrbank.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record DepartmentCreateRequest(
    
    @NotBlank(message = "부서 이름은 필수입니다")
    String name,
    String description,
    LocalDate establishedDate
) {

}
