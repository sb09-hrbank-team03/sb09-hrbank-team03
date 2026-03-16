package com.sb09.hrbank.dto.response;

public record DiffDto(
    String propertyName,
    String before,
    String after
) {
}
