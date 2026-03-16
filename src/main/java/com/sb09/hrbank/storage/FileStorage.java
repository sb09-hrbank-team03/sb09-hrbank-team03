package com.sb09.hrbank.storage;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class FileStorage {

  public Resource load(String path) {
    return new FileSystemResource(path);
  }

  public void delete(String path) {
    new FileSystemResource(path).getFile().delete();
  }
}