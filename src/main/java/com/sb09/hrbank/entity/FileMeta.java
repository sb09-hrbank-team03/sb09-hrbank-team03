package com.sb09.hrbank.entity;

import com.sb09.hrbank.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "binary_contents")
public class FileMeta extends BaseUpdatableEntity {

  @Column(name = "file_name", nullable = false)
  private String fileName;

  @Column(nullable = false)
  private Long size;

  @Column(name = "content_type", nullable = false)
  private String contentType;

  @Column(nullable = false)
  private String path;

  public FileMeta(String name, long size, String contentType, String path) {
    this.fileName = name;
    this.size = size;
    this.contentType = contentType;
    this.path = path;
  }
}