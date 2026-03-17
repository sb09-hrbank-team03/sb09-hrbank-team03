package com.sb09.hrbank.entity;

import com.sb09.hrbank.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "departments")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Department extends BaseUpdatableEntity {

  @Column(unique = true, nullable = false, length = 100)
  private String name;

  @Column(length = 255)
  private String description;

  @Column(nullable = false)
  private LocalDate establishedDate;

  @Builder
  public Department(String name, String description, LocalDate establishedDate) {
    this.name = name;
    this.description = description;
    this.establishedDate = establishedDate;
  }

  public void update(String name, String description, LocalDate establishedDate) {
    this.name = name;
    this.description = description;
    this.establishedDate = establishedDate;
  }
}
