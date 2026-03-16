package com.sb09.hrbank.service.basic;

import com.sb09.hrbank.dto.response.BackupDto;
import com.sb09.hrbank.entity.BackupHistory;
import com.sb09.hrbank.entity.BackupStatus;
import com.sb09.hrbank.mapper.BackupMapper;
import com.sb09.hrbank.repository.BackupRepository;
import com.sb09.hrbank.service.BackupService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicBackupService implements BackupService {

  private final BackupRepository backupRepository;
  private final BackupMapper backupMapper;

  @Override
  @Transactional
  public BackupDto backup(String ip) {

    validateBackupNotRunning();

    BackupHistory history = BackupHistory.start(ip);

    try {
      BackupHistory saved = backupRepository.save(history);
      return backupMapper.toDto(saved);

    } catch (DataIntegrityViolationException e) {
      throw new IllegalStateException("이미 진행중인 백업이 있습니다.");
    }
  }

  private void validateBackupNotRunning() {
    if (backupRepository.existsByBackupStatus(BackupStatus.IN_PROGRESS)) {
      throw new IllegalStateException("이미 진행중인 백업이 있습니다.");
    }
  }
}