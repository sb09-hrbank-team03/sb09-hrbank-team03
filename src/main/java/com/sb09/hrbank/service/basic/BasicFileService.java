package com.sb09.hrbank.service.basic;

import com.sb09.hrbank.entity.FileMeta;
import com.sb09.hrbank.repository.FileMetaRepository;
import com.sb09.hrbank.service.FileService;
import com.sb09.hrbank.storage.FileStorage;
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
        .orElseThrow(() -> new NoSuchElementException("파일 없음"));
    return storage.load(file.getPath());
  }

  @Override
  public FileMeta save(Path path) {

    FileMeta file = new FileMeta(
        path.getFileName().toString(),
        path.toFile().length(),
        "text/csv",
        path.toString()
    );

    return repository.save(file);
  }
}