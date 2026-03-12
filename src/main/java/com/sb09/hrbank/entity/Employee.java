package com.sb09.hrbank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Table(name = "employees")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Employee {

  @Id
  @UuidGenerator
  private UUID id;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private Instant updatedAt;

  @Column(name = "hire_date", nullable = false)
  private LocalDate hireDate;

  @Column(nullable = false, length = 50)
  private String name;

  @Column(nullable = false, unique = true, length = 100)
  private String email;

  @Column(name = "employee_number", nullable = false, unique = true, length = 100)
  private String employeeNumber;

  @Column(nullable = false, length = 50)
  private String position;

  @Enumerated(EnumType.STRING)
  @Column(name = "work_status", nullable = false, length = 50)
  private WorkStatus status;

  @Column(name = "department_id", nullable = false)
  private UUID departmentId;

  @Column(name = "profile_id")
  private UUID profileImageId;

  private Employee(
      LocalDate hireDate,
      String name,
      String email,
      String employeeNumber,
      String position,
      WorkStatus status,
      UUID departmentId,
      UUID profileImageId
  ) {
    this.hireDate = hireDate;
    this.name = name;
    this.email = email;
    this.employeeNumber = employeeNumber;
    this.position = position;
    this.status = status;
    this.departmentId = departmentId;
    this.profileImageId = profileImageId;
  }

  public static Employee create(
      LocalDate hireDate,
      String name,
      String email,
      String employeeNumber,
      String position,
      UUID departmentId,
      UUID profileImageId
  ) {
    return new Employee(
        hireDate,
        name,
        email,
        employeeNumber,
        position,
        WorkStatus.ACTIVE,
        departmentId,
        profileImageId
    );
  }

  public void updateProfileImage(UUID profileImageId) {
    this.profileImageId = profileImageId;
  }
}
