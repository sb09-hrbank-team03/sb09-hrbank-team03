package com.sb09.hrbank.service;

import org.springframework.core.io.Resource;

public interface FileService {
  Resource download(Long id);

  void delete(Long id);
}