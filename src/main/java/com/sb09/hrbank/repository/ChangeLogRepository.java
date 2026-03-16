package com.sb09.hrbank.repository;

import com.sb09.hrbank.entity.ChangeLog;
import com.sb09.hrbank.entity.ChangeType;
import java.time.Instant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {

  @Query("SELECT COUNT(c) FROM ChangeLog c WHERE c.createdAt >= :fromDate AND c.createdAt <= :toDate")
  Long countChangeLogByDuration(
      @Param("fromDate") Instant fromDate,
      @Param("toDate") Instant toDate
  );

  @Query("SELECT c FROM ChangeLog c "
      + "WHERE (:employeeNumber IS NULL OR c.employeeNumber LIKE %:employeeNumber%) "
      + "AND (:memo IS NULL OR c.memo LIKE %:memo%) "
      + "AND (:ipAddress IS NULL OR c.ipAddress LIKE %:ipAddress%) "
      + "AND (:type IS NULL OR c.type = :type) "
      + "AND (:atFrom IS NULL OR c.createdAt >= :atFrom) "
      + "AND (:atTo IS NULL OR c.createdAt <= :atTo) "
      + "AND (:idAfter IS NULL OR c.id < :idAfter)")
  Slice<ChangeLog> findAllByCondition(
      @Param("employeeNumber") String employeeNumber,
      @Param("memo") String memo,
      @Param("ipAddress") String ipAddress,
      @Param("type") ChangeType type,
      @Param("atFrom") Instant atFrom,
      @Param("atTo") Instant atTo,
      @Param("idAfter") Long idAfter,
      Pageable pageable
  );
}
