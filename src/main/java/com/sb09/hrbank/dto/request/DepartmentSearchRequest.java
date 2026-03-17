package com.sb09.hrbank.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

@Getter
@Setter
@NoArgsConstructor
public class DepartmentSearchRequest {

  private String keyword;

  private String cursor;

  private Long lastElementId;

  @Min(1)
  @Max(100)
  private int size = 10;

  private DepartmentSortField sortBy = DepartmentSortField.establishedDate;

  private Sort.Direction sortDirection = Sort.Direction.DESC;
}
