package com.sb09.hrbank.dto.request;

import com.sb09.hrbank.entity.WorkStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeSearchRequest {

  private String nameOrEmail;

  private String departmentName;

  private String position;

  private String employeeNumber;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate hireDateFrom;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate hireDateTo;

  private WorkStatus status;

  private String cursor;

  private Long idAfter;

  @Min(1)
  @Max(100)
  private int size = 10;

  private EmployeeSortField sortField = EmployeeSortField.name;

  private Sort.Direction sortDirection = Sort.Direction.ASC;

}