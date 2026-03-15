package com.sb09.hrbank.service.basic;

import com.sb09.hrbank.dto.common.CursorPageResponse;
import com.sb09.hrbank.dto.request.BackupListRequest;
import com.sb09.hrbank.dto.response.BackupDto;
import com.sb09.hrbank.entity.BackupHistory;
import com.sb09.hrbank.entity.BackupStatus;
import com.sb09.hrbank.mapper.BackupMapper;
import com.sb09.hrbank.mapper.CursorPageResponseMapper;
import com.sb09.hrbank.repository.BackupRepository;
import com.sb09.hrbank.service.BackupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicBackupService implements BackupService {

  private final BackupRepository backupRepository;
  private final BackupMapper backupMapper;
  private final CursorPageResponseMapper cursorMapper;

  @Override
  @Transactional
  public BackupDto backup(String ip) {

    if (backupRepository.existsByBackupStatus(BackupStatus.IN_PROGRESS)) {
      throw new IllegalStateException("이미 진행중인 백업이 있습니다.");
    }

    BackupHistory history = BackupHistory.start(ip);

    BackupHistory saved = backupRepository.save(history);

    return backupMapper.toDto(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public CursorPageResponse<BackupDto> getBackups(BackupListRequest request) {

    Slice<BackupHistory> slice =
        backupRepository.searchBackups(request);

    return cursorMapper.fromSlice(
        slice,
        backupMapper::toDto,
        BackupHistory::getStartedAt,
        BackupHistory::getId
    );
  }
}