package com.sb09.hrbank.entity;

import com.sb09.hrbank.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employee_history_details")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangeLogDetail extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "history_id", nullable = false)
  private ChangeLog changeLog;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String property;

  @Column(columnDefinition = "TEXT")
  private String beforeValue;

  @Column(columnDefinition = "TEXT")
  private String afterValue;

  public ChangeLogDetail(ChangeLog changeLog, String property, String beforeValue, String afterValue){
    this.changeLog = changeLog;
    this.property = property;
    this.beforeValue = beforeValue;
    this.afterValue = afterValue;
  }
}
