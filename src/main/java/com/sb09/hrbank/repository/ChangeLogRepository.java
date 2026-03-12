package com.sb09.hrbank.repository;

import com.sb09.hrbank.entity.ChangeLog;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangeLogRepository extends JpaRepository<ChangeLog, UUID> {

}
