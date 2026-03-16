package com.sb09.hrbank.repository;

import com.sb09.hrbank.entity.BackupHistory;
import com.sb09.hrbank.entity.BackupStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BackupRepository
    extends JpaRepository<BackupHistory, Long>,
    BackupRepositoryCustom {

  boolean existsByBackupStatus(BackupStatus backupStatus);

  Optional<BackupHistory> findFirstByBackupStatusOrderByStartedAtDesc(BackupStatus status);
}