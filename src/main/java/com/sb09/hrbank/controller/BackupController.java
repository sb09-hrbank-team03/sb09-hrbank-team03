package com.sb09.hrbank.controller;

import com.sb09.hrbank.dto.common.CursorPageResponse;
import com.sb09.hrbank.dto.request.BackupListRequest;
import com.sb09.hrbank.dto.response.BackupDto;
import com.sb09.hrbank.service.BackupService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/backups")
@RequiredArgsConstructor
public class BackupController {

  private final BackupService backupService;

  @PostMapping
  public BackupDto backup(
      @RequestHeader(value = "X-Forwarded-For", required = false) String ip,
      HttpServletRequest request
  ) {
    String clientIp;
    if (ip != null && !ip.isBlank()) {
      clientIp = ip.split(",")[0].trim();
    } else {
      clientIp = request.getRemoteAddr();
    }

    return backupService.backup(clientIp);
  }

  @GetMapping
  public CursorPageResponse<BackupDto> getBackups(
      @Valid @ModelAttribute BackupListRequest request
  ) {
    return backupService.getBackups(request);
  }
}