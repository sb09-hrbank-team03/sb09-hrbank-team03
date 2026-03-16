package com.sb09.hrbank.service;

import com.sb09.hrbank.dto.common.CursorPageResponse;
import com.sb09.hrbank.dto.request.BackupListRequest;
import com.sb09.hrbank.dto.response.BackupDto;

public interface BackupService {

  BackupDto backup(String ip);

  CursorPageResponse<BackupDto> getBackups(BackupListRequest request);
}