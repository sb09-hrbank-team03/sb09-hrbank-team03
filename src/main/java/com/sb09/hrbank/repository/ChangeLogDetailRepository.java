package com.sb09.hrbank.repository;

import com.sb09.hrbank.entity.ChangeLogDetail;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangeLogDetailRepository extends JpaRepository<ChangeLogDetail, UUID> {

}
