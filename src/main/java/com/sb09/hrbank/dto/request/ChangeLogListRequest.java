package com.sb09.hrbank.dto.request;

import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChangeLogListRequest {
  private String employeeNumber;
  private String type;
  private String memo;
  private String ipAddress;
  private Instant atFrom;
  private Instant atTo;
  private Long idAfter;
  private String cursor;
  private Integer size;
  private String sortField;
  private String sortDirection;
}
