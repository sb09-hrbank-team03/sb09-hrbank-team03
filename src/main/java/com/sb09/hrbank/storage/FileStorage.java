package com.sb09.hrbank.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileStorage {

  private static final Path ROOT_PATH = Path.of("storage");

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

  public Path store(MultipartFile file, String directory) {
    try {
      Path targetDirectory = ROOT_PATH.resolve(directory).normalize().toAbsolutePath();
      Files.createDirectories(targetDirectory);

      String extension = extractExtension(file.getOriginalFilename());
      String storedFileName = UUID.randomUUID() + extension;
      Path targetPath = targetDirectory.resolve(storedFileName);

      try (InputStream inputStream = file.getInputStream()) {
        Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
      }

      return targetPath;
    } catch (IOException e) {
      throw new RuntimeException("파일 저장 중 I/O 오류가 발생했습니다.", e);
    }
  }

  private String extractExtension(String fileName) {
    if (fileName == null || fileName.isBlank()) {
      return "";
    }

    int lastDot = fileName.lastIndexOf('.');
    if (lastDot < 0 || lastDot == fileName.length() - 1) {
      return "";
    }

    return fileName.substring(lastDot);
  }
}