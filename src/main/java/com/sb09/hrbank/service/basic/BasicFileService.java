package com.sb09.hrbank.service.basic;

import com.sb09.hrbank.entity.FileMeta;
import com.sb09.hrbank.repository.FileMetaRepository;
import com.sb09.hrbank.service.FileService;
import com.sb09.hrbank.storage.FileStorage;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicFileService implements FileService {

  private static final String PROFILE_IMAGES_DIR = "profile-images";

  private final FileMetaRepository repository;
  private final FileStorage storage;

  @Override
  public Resource download(Long id) {
    FileMeta file = findFile(id);
    return storage.load(file.getPath());
  }

  @Override
  public void delete(Long id) {
    FileMeta file = findFile(id);
    try {
      storage.delete(file.getPath());
    } catch (RuntimeException e) {
      log.warn("파일 삭제 중 오류가 발생했습니다. 메타데이터는 계속 삭제합니다. id={}, path={}", id, file.getPath(), e);
    } finally {
      repository.deleteById(id);
    }
  }

  @Override
  public FileMeta save(Path path) {
    return repository.save(toFileMeta(path, path.toFile().length(), "text/csv"));
  }

  @Override
  public FileMeta saveProfileImage(MultipartFile profileImage) {
    if (profileImage == null || profileImage.isEmpty()) {
      throw new IllegalArgumentException("업로드할 프로필 이미지가 비어 있습니다.");
    }

    String contentType = profileImage.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
      throw new IllegalArgumentException("프로필 이미지는 image/* 타입만 업로드할 수 있습니다.");
    }

    Path savedPath = storage.store(profileImage, PROFILE_IMAGES_DIR);

    if (TransactionSynchronizationManager.isSynchronizationActive()) {
      registerRollbackCleanup(savedPath);
    }

    FileMeta file = toFileMeta(savedPath, profileImage.getSize(), contentType);
    try {
      return repository.save(file);
    } catch (RuntimeException e) {
      if (!TransactionSynchronizationManager.isSynchronizationActive()) {
        deleteStoredFileQuietly(savedPath);
      }
      throw e;
    }
  }

  private void registerRollbackCleanup(Path savedPath) {
    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCompletion(int status) {
        if (status == STATUS_ROLLED_BACK) {
          deleteStoredFileQuietly(savedPath);
        }
      }
    });
  }

  private void deleteStoredFileQuietly(Path savedPath) {
    try {
      storage.delete(savedPath.toString());
    } catch (RuntimeException e) {
      log.warn("롤백 보상 파일 삭제에 실패했습니다. path={}", savedPath, e);
    }
  }

  private FileMeta findFile(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 파일을 찾을 수 없습니다. id=" + id));
  }

  private FileMeta toFileMeta(Path path, long size, String contentType) {
    return new FileMeta(
        path.getFileName().toString(),
        size,
        contentType,
        path.toString()
    );
  }
}