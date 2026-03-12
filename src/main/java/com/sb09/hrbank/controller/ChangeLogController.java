package com.sb09.hrbank.controller;

import com.sb09.hrbank.dto.response.ChangeLogDetailDto;
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
  public ResponseEntity<?> list(
      @RequestParam String employeeNumber,
      @RequestParam String type,
      @RequestParam String memo,
      @RequestParam String ipAddress,
      @RequestParam String atFrom,
      @RequestParam String atTo,
      @RequestParam int idAfter,
      @RequestParam String cursor,
      @RequestParam int size,
      @RequestParam String sortField,
      @RequestParam String sortDirection
  ){

   // changeLogService.list();
    //return ResponseEntity.ok();
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
   @RequestParam Instant fromDate,
   @RequestParam Instant toDate
  ){
     Long count = changeLogService.getCount(fromDate, toDate);
     return ResponseEntity.ok(count);
  }


}
