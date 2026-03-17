package com.sb09.hrbank.dto.request;

import com.sb09.hrbank.entity.ChangeType;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

@Getter
@Setter
@NoArgsConstructor
public class ChangeLogListRequest{
  private String employeeNumber;
  private ChangeType type;
  private String memo;
  private String ipAddress;
  private Instant atFrom;
  private Instant atTo;
  private Long idAfter;
  private Instant cursor;

  private int size = 10;
  private ChangeLogSortField sortField = ChangeLogSortField.at;
  private Sort.Direction sortDirection = Sort.Direction.DESC;
}
