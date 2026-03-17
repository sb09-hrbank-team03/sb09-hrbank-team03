package com.sb09.hrbank.repository;

import com.sb09.hrbank.entity.ChangeLog;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

// specification 작동되게 할거라 JpaSpecificationExecutor 추가
public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long>,
    JpaSpecificationExecutor<ChangeLog> {
  Long countByCreatedAtBetween(Instant fromDate, Instant toDate);
}
