package com.sb09.hrbank.repository;

import com.sb09.hrbank.entity.ChangeLog;
import com.sb09.hrbank.entity.ChangeLogDetail;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangeLogDetailRepository extends JpaRepository<ChangeLogDetail, Long> {
  List<ChangeLogDetail> findByChangeLogId(Long id);
}
