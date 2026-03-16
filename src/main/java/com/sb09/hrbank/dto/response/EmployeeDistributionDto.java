package com.sb09.hrbank.dto.response;

public record EmployeeDistributionDto(
    String groupKey,
    Long count,
    double percentage
) {

}
