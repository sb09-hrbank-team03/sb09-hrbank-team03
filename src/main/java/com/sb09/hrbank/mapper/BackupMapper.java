package com.sb09.hrbank.mapper;

import com.sb09.hrbank.dto.response.BackupDto;
import com.sb09.hrbank.entity.BackupHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BackupMapper {

  @Mapping(source = "ipAddress", target = "worker")

  @Mapping(source = "backupStatus", target = "status")

  @Mapping(source = "file.id", target = "fileId")

  BackupDto toDto(BackupHistory entity);
}