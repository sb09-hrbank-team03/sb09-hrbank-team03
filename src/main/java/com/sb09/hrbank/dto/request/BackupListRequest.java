package com.sb09.hrbank.dto.request;

import com.sb09.hrbank.entity.BackupStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class BackupListRequest {

  private String worker;

  private BackupStatus status;

  private Instant startedAtFrom;

  private Instant startedAtTo;

  private String cursor;

  private Long idAfter;

  @Min(1)
  @Max(100)
  private int size = 10;

  private BackupSortField sortField = BackupSortField.startedAt;

  private Sort.Direction sortDirection = Sort.Direction.DESC;
}