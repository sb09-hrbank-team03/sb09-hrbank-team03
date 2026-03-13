package com.sb09.hrbank.controller;

import com.sb09.hrbank.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

  private final FileService fileService;

  @GetMapping("/{id}/download")
  public ResponseEntity<Resource> download(@PathVariable Long id) {
    Resource resource = fileService.download(id);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(resource);
  }
}