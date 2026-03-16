package com.sb09.hrbank.entity;

import com.sb09.hrbank.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="employee_histories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangeLog extends BaseEntity {

  @Enumerated(EnumType.STRING)
  @Column(name = "change_type", length = 50, nullable = false)
  private ChangeType type;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "employee_id")
  private Employee employee;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String ipAddress;

  @Column(columnDefinition = "TEXT")
  private String memo;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String employeeNumber;

  public ChangeLog(ChangeType type, Employee employee, String ipAddress, String memo, String employeeNumber){
    this.type = type;
    this.employee = employee;
    this.ipAddress = ipAddress;
    this.memo = memo;
    this.employeeNumber = employeeNumber;
  }

}
