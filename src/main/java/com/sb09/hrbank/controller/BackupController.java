package com.sb09.hrbank.controller;

import com.sb09.hrbank.dto.response.BackupDto;
import com.sb09.hrbank.service.BackupService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/backups")
public class BackupController {

  private final BackupService backupService;

  @PostMapping
  public BackupDto backup(@RequestHeader(value = "X-Forwarded-For", required = false) String ip,
      HttpServletRequest request) {

    String clientIp = ip != null ? ip : request.getRemoteAddr();
    return backupService.backup(clientIp);
  }
}
