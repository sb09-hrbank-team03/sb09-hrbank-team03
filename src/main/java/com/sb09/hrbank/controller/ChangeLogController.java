package com.sb09.hrbank.controller;

import com.sb09.hrbank.dto.common.CursorPageResponse;
import com.sb09.hrbank.dto.request.ChangeLogListRequest;
import com.sb09.hrbank.dto.response.ChangeLogDetailDto;
import com.sb09.hrbank.dto.response.ChangeLogDto;
import com.sb09.hrbank.service.ChangeLogService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/change-logs")
@RequiredArgsConstructor
public class ChangeLogController {
  private final ChangeLogService changeLogService;

  @GetMapping("")
  public ResponseEntity<?> history(ChangeLogListRequest request){
    CursorPageResponse<ChangeLogDto> dto = changeLogService.history(request);
    return ResponseEntity.ok(dto);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ChangeLogDetailDto> getDetails(
      @PathVariable Long id
  ) {
     ChangeLogDetailDto details = changeLogService.getDetails(id);
     return ResponseEntity.ok(details);
  }

  @GetMapping("/count")
  public ResponseEntity<Long> getCount(
   @RequestParam(required = false) Instant fromDate,
   @RequestParam(required = false) Instant toDate
  ){
     Long count = changeLogService.getCount(fromDate, toDate);
     return ResponseEntity.ok(count);
  }
}
