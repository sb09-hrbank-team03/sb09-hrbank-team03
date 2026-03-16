package com.sb09.hrbank.service;

import com.sb09.hrbank.dto.response.BackupDto;

public interface BackupService {

  BackupDto backup(String ip);

}