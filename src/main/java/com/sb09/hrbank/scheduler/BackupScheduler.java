package com.sb09.hrbank.scheduler;

import com.sb09.hrbank.service.BackupService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BackupScheduler {

  private final BackupService backupService;

  @Scheduled(
      fixedDelayString = "${backup.schedule:3600000}",
      initialDelayString = "${backup.schedule:3600000}"
  )
  public void runBackup() {

    if (backupService.isRecentlyBackedUp()) {
      return;
    }

    backupService.backup("system");
  }
}