package com.sb09.hrbank.dto.request;

import com.sb09.hrbank.entity.ChangeType;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChangeLogListRequest {
  private String employeeNumber;
  private ChangeType type;
  private String memo;
  private String ipAddress;
  private Instant atFrom;
  private Instant atTo;
  private Long idAfter;
  private String cursor;
  private int size = 10;
  private String sortField = "at";
  private String sortDirection = "desc";
}
