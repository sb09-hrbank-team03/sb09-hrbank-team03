package com.sb09.hrbank.service.basic;

import com.sb09.hrbank.entity.FileMeta;
import com.sb09.hrbank.repository.FileMetaRepository;
import com.sb09.hrbank.service.FileService;
import com.sb09.hrbank.storage.FileStorage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicFileService implements FileService {

  private final FileMetaRepository repository;
  private final FileStorage storage;

  @Override
  public Resource download(Long id) {
    FileMeta file = repository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 파일을 찾을 수 없습니다. id=" + id));
    return storage.load(file.getPath());
  }

  @Override
  public FileMeta save(Path path) {

    try {
      String contentType = Files.probeContentType(path);

      FileMeta file = new FileMeta(
          path.getFileName().toString(),
          Files.size(path),
          contentType,
          path.toString()
      );

      return repository.save(file);

    } catch (Exception e) {
      throw new RuntimeException("파일 메타 저장 실패", e);
    }
  }
}