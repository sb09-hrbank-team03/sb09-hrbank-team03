package com.sb09.hrbank.repository;

import com.sb09.hrbank.entity.FileMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileMetaRepository extends JpaRepository<FileMeta, Long> {
}