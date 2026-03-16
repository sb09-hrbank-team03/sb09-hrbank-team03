package com.sb09.hrbank.storage;

import java.util.NoSuchElementException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class FileStorage {

  public Resource load(String path) {
    return new FileSystemResource(path);
  }

  public void delete(String path) {
    try {
      boolean deleted = Files.deleteIfExists(Path.of(path));
      if (!deleted) {
        throw new NoSuchElementException("삭제할 파일이 존재하지 않습니다. path=" + path);
      }
    } catch (IOException e) {
      throw new RuntimeException("파일 삭제 중 I/O 오류가 발생했습니다. path=" + path, e);
    }
  }
}