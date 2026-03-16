package com.sb09.hrbank.mapper;

import com.sb09.hrbank.dto.response.ChangeLogDto;
import com.sb09.hrbank.entity.ChangeLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChangeLogMapper {
  @Mapping(source = "createdAt", target = "at")
  ChangeLogDto toDto(ChangeLog changeLog);
}
