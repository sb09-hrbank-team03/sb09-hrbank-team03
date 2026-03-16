package com.sb09.hrbank.service;

import com.sb09.hrbank.entity.FileMeta;
import java.nio.file.Path;
import org.springframework.core.io.Resource;

public interface FileService {
  Resource download(Long id);
  FileMeta save(Path path);


  void delete(Long id);
}