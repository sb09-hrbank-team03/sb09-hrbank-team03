package com.sb09.hrbank.entity;

import com.sb09.hrbank.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "backups")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BackupHistory extends BaseEntity {

  private String ipAddress;

  @Enumerated(EnumType.STRING)
  private BackupStatus backupStatus;

  private Instant startedAt;

  private Instant endedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "file_id")
  private FileMeta file;

  public BackupHistory(String ipAddress, BackupStatus backupStatus, Instant startedAt) {
    this.ipAddress = ipAddress;
    this.backupStatus = backupStatus;
    this.startedAt = startedAt;
  }

  public static BackupHistory start(String ipAddress) {
    return new BackupHistory(
        ipAddress,
        BackupStatus.IN_PROGRESS,
        Instant.now()
    );
  }

  public void complete(FileMeta file, Instant endedAt) {
    this.file = file;
    this.endedAt = endedAt;
    this.backupStatus = BackupStatus.COMPLETED;
  }

  public void fail(FileMeta file, Instant endedAt) {
    this.file = file;
    this.endedAt = endedAt;
    this.backupStatus = BackupStatus.FAILED;
  }

  public void skip(Instant endedAt) {
    this.endedAt = endedAt;
    this.backupStatus = BackupStatus.SKIPPED;
  }
}