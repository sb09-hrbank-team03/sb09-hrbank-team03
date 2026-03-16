package com.sb09.hrbank.service.basic;

import com.sb09.hrbank.backup.CsvBackupWriter;
import com.sb09.hrbank.dto.common.CursorPageResponse;
import com.sb09.hrbank.dto.request.BackupListRequest;
import com.sb09.hrbank.dto.response.BackupDto;
import com.sb09.hrbank.entity.BackupHistory;
import com.sb09.hrbank.entity.BackupStatus;
import com.sb09.hrbank.entity.Employee;
import com.sb09.hrbank.entity.FileMeta;
import com.sb09.hrbank.mapper.BackupMapper;
import com.sb09.hrbank.mapper.CursorPageResponseMapper;
import com.sb09.hrbank.repository.BackupRepository;
import com.sb09.hrbank.repository.EmployeeRepository;
import com.sb09.hrbank.service.BackupService;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicBackupService implements BackupService {

  private static final int BATCH_SIZE = 100;

  private final BackupRepository backupRepository;
  private final BackupMapper backupMapper;
  private final CursorPageResponseMapper cursorMapper;

  private final EmployeeRepository employeeRepository;
  private final BasicFileService fileService;
  private final CsvBackupWriter csvBackupWriter;

  @Override
  @Transactional
  public BackupDto backup(String ip) {

    if (backupRepository.existsByBackupStatus(BackupStatus.IN_PROGRESS)) {
      throw new IllegalStateException("이미 진행중인 백업이 있습니다.");
    }

    boolean needBackup = isBackupRequired();

    BackupHistory history = backupRepository.save(
        BackupHistory.start(ip)
    );

    if (!needBackup) {
      history.skip(Instant.now());
      return backupMapper.toDto(history);
    }

    try {

      FileMeta file = executeBackup();

      history.complete(file, Instant.now());

    } catch (Exception e) {

      log.error("백업 수행 중 오류 발생", e);

      FileMeta logFile = null;

      try {
        logFile = createErrorLog(e);
      } catch (IOException logException) {
        log.error("백업 에러 로그 생성 실패", logException);
      }

      history.fail(logFile, Instant.now());
    }

    return backupMapper.toDto(history);
  }

  /**
   * STEP1
   * 최근 완료된 백업 이후 직원 데이터 변경 여부 확인
   */
  private boolean isBackupRequired() {

    return backupRepository
        .findFirstByBackupStatusOrderByStartedAtDesc(BackupStatus.COMPLETED)
        .map(last -> employeeRepository.existsByUpdatedAtAfter(last.getStartedAt()))
        .orElse(true);
  }

  @Override
  @Transactional(readOnly = true)
  public CursorPageResponse<BackupDto> getBackups(BackupListRequest request) {

    var slice = backupRepository.searchBackups(request);

    return cursorMapper.fromSlice(
        slice,
        backupMapper::toDto,
        BackupHistory::getStartedAt,
        BackupHistory::getId
    );
  }

  /**
   * STEP3 실제 백업 수행 (Cursor 기반 조회)
   */
  private FileMeta executeBackup() throws IOException {

    Path path = csvBackupWriter.createCsv();

    try (BufferedWriter writer = Files.newBufferedWriter(path)) {

      csvBackupWriter.writeHeader(writer);

      Long lastId = 0L;

      while (true) {

        List<Employee> employees =
            employeeRepository.findTop100ByIdGreaterThanOrderByIdAsc(lastId);

        if (employees.isEmpty()) {
          break;
        }

        for (Employee e : employees) {

          writer.write(
              escape(e.getEmployeeNumber()) + "," +
                  escape(e.getName()) + "," +
                  escape(e.getEmail()) + "," +
                  escape(e.getDepartment() != null ? e.getDepartment().getName() : "") + "," +
                  escape(e.getPosition()) + "," +
                  escape(e.getStatus().name()) + "," +
                  escape(String.valueOf(e.getHireDate()))
          );

          writer.newLine();

          lastId = e.getId();
        }
      }
    }

    return fileService.save(path);
  }

  /**
   * CSV 깨짐 방지
   */
  private String escape(String value) {

    if (value == null) {
      return "";
    }

    return "\"" + value.replace("\"", "\"\"") + "\"";
  }

  /**
   * STEP4 실패 시 에러 로그 저장
   */
  private FileMeta createErrorLog(Exception e) throws IOException {

    Path path = Path.of("backup/error-" + System.currentTimeMillis() + ".log");

    Files.createDirectories(path.getParent());

    Files.writeString(path, e.getMessage());

    return fileService.save(path);
  }
}