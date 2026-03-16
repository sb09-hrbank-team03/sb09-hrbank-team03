package com.sb09.hrbank.service;

import com.sb09.hrbank.entity.FileMeta;
import java.nio.file.Path;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
  Resource download(Long id);

  void delete(Long id);

  FileMeta save(Path path);

  FileMeta saveProfileImage(MultipartFile profileImage);

}