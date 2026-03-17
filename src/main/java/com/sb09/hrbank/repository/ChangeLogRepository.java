package com.sb09.hrbank.repository;

import com.sb09.hrbank.entity.ChangeLog;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long>,
    ChangeLogRepositoryCustom {
  Long countByCreatedAtBetween(Instant fromDate, Instant toDate);
}
