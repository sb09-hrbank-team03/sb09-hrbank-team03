package com.sb09.hrbank.mapper;

import com.sb09.hrbank.dto.response.ChangeLogDetailDto;
import com.sb09.hrbank.dto.response.DiffDto;
import com.sb09.hrbank.entity.ChangeLog;
import com.sb09.hrbank.entity.ChangeLogDetail;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChangeLogDetailMapper {
  @Mapping(source = "changeLog.id", target = "id")
  @Mapping(source = "changeLog.type", target = "type")
  @Mapping(source = "changeLog.employeeNumber", target = "employeeNumber")
  @Mapping(source = "changeLog.memo", target = "memo")
  @Mapping(source = "changeLog.ipAddress", target = "ipAddress")
  @Mapping(source = "changeLog.createdAt", target = "at")
  @Mapping(source = "changeLog.employee.name", target = "employeeName")
  @Mapping(source = "changeLog.employee.profileImage.id", target = "profileImageId")
  @Mapping(source = "diffs", target = "diffs")
  ChangeLogDetailDto toDetailDto(ChangeLog changeLog, List<DiffDto> diffs);

  @Mapping(source = "property",    target = "property")
  @Mapping(source = "beforeValue", target = "before")
  @Mapping(source = "afterValue",  target = "after")
  DiffDto toDiffDto(ChangeLogDetail detail);
  List<DiffDto> toDiffDtoList(List<ChangeLogDetail> details);
}
