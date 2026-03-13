package com.sb09.hrbank.entity;

import com.sb09.hrbank.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Table(name = "employees")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Employee extends BaseUpdatableEntity {

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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "department_id", nullable = false)
  private Department department;

  @Column(name = "profile_id")
  private Long profileImageId;

  private Employee(
      LocalDate hireDate,
      String name,
      String email,
      String employeeNumber,
      String position,
      WorkStatus status,
      Department department,
      Long profileImageId
  ) {
    this.hireDate = hireDate;
    this.name = name;
    this.email = email;
    this.employeeNumber = employeeNumber;
    this.position = position;
    this.status = status;
    this.department = department;
    this.profileImageId = profileImageId;
  }

  public static Employee create(
      LocalDate hireDate,
      String name,
      String email,
      String employeeNumber,
      String position,
      Department department,
      Long profileImageId
  ) {
    return new Employee(
        hireDate,
        name,
        email,
        employeeNumber,
        position,
        WorkStatus.ACTIVE,
        department,
        profileImageId
    );
  }

  public void updateProfileImage(Long profileImageId) {
    this.profileImageId = profileImageId;
  }
}
