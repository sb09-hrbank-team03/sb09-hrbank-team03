package com.sb09.hrbank.repository;

import com.sb09.hrbank.entity.ChangeLog;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChangeLogRepository extends JpaRepository<ChangeLog, UUID> {

  @Query("SELECT c From ChangeLog c "
      + "WHERE (:employeeNumber is NULL OR c.employeeNumber LIKE %:employeeNumber%) "
      + "AND (:memo IS NULL OR c.memo LIKE %:memo%) "
      + "AND (:ipAddress IS NULL OR c.ipAddress LIKe %:ipAddress%) "
      + "AND (:type IS NULL OR c.type = :type) "
      + "AND (:atFrom IS NULL OR c.createdAt >= :atFrom) "
      + "AND (:atTo IS NULL OR c.createdAt <= :atTo) "
      + "AND (:idAfter IS NULL OR c.id < :idAfter)")
  Slice<ChangeLog> findAllByCondition(
      @Param("employeeNumber") String employeeNumber,
      @Param("memo") String memo,
      @Param("ipAddress") String ipAddress,
      @Param("type") String type,
      @Param("atFrom") Instant atFrom,
      @Param("atTo") Instant atTo,
      @Param("idAfter") Long idAfter,
      Pageable pageable
  );
}
