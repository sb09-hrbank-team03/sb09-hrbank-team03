package com.sb09.hrbank.mapper;

import com.sb09.hrbank.dto.response.BackupDto;
import com.sb09.hrbank.entity.BackupHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface BackupMapper {

  ZoneId KST = ZoneId.of("Asia/Seoul");
  DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

  @Mapping(source = "ipAddress", target = "worker")
  @Mapping(source = "backupStatus", target = "status")
  @Mapping(source = "file.id", target = "fileId")
  @Mapping(source = "startedAt", target = "startedAt")
  @Mapping(source = "endedAt", target = "endedAt")
  BackupDto toDto(BackupHistory entity);

  default String map(Instant instant) {
    if (instant == null) return null;
    return instant.atZone(KST).format(FORMATTER);
  }
}