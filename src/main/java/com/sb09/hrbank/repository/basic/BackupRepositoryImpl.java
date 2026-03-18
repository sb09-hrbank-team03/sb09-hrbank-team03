package com.sb09.hrbank.repository.basic;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sb09.hrbank.dto.request.BackupListRequest;
import com.sb09.hrbank.dto.request.BackupSortField;
import com.sb09.hrbank.entity.BackupHistory;
import com.sb09.hrbank.entity.BackupStatus;
import com.sb09.hrbank.entity.QBackupHistory;
import com.sb09.hrbank.repository.BackupRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BackupRepositoryImpl implements BackupRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  private final QBackupHistory backup = QBackupHistory.backupHistory;

  @Override
  public Slice<BackupHistory> searchBackups(BackupListRequest request) {

    boolean isDesc = request.getSortDirection() == Sort.Direction.DESC;
    OrderSpecifier<?> order = getOrder(request);
    OrderSpecifier<?> idOrder = isDesc ? backup.id.desc() : backup.id.asc();

    List<BackupHistory> results = queryFactory
        .selectFrom(backup)
        .where(
            workerContains(request.getWorker()),
            statusEq(request.getStatus()),
            startedAtGoe(request.getStartedAtFrom()),
            startedAtLoe(request.getStartedAtTo()),
            cursorCondition(request)
        )
        .orderBy(order, idOrder)
        .limit(request.getSize() + 1)
        .fetch();

    boolean hasNext = results.size() > request.getSize();

    if (hasNext) {
      results.remove(request.getSize());
    }

    return new SliceImpl<>(
        results,
        PageRequest.of(0, request.getSize()),
        hasNext
    );
  }

  private BooleanExpression workerContains(String worker) {
    if (worker == null || worker.isBlank()) {
      return null;
    }
    return backup.ipAddress.contains(worker);
  }

  private BooleanExpression statusEq(BackupStatus status) {
    if (status == null) {
      return null;
    }
    return backup.backupStatus.eq(status);
  }

  private BooleanExpression startedAtGoe(Instant from) {
    if (from == null) {
      return null;
    }
    return backup.startedAt.goe(from);
  }

  private BooleanExpression startedAtLoe(Instant to) {
    if (to == null) {
      return null;
    }
    return backup.startedAt.loe(to);
  }

  private BooleanExpression cursorCondition(BackupListRequest request) {

    if (request.getIdAfter() == null) {
      return null;
    }

    boolean isDesc = request.getSortDirection() == Sort.Direction.DESC;
    String cursor = request.getCursor();

    if (request.getSortField() == BackupSortField.startedAt && cursor != null) {
      Instant cursorInstant = Instant.parse(cursor);
      if (isDesc) {
        return backup.startedAt.lt(cursorInstant)
            .or(backup.startedAt.eq(cursorInstant)
                .and(backup.id.lt(request.getIdAfter())));
      } else {
        return backup.startedAt.gt(cursorInstant)
            .or(backup.startedAt.eq(cursorInstant)
                .and(backup.id.gt(request.getIdAfter())));
      }
    }

    if (request.getSortField() == BackupSortField.endedAt && cursor != null) {
      Instant cursorInstant = Instant.parse(cursor);
      if (isDesc) {
        return backup.endedAt.lt(cursorInstant)
            .or(backup.endedAt.eq(cursorInstant)
                .and(backup.id.lt(request.getIdAfter())));
      } else {
        return backup.endedAt.gt(cursorInstant)
            .or(backup.endedAt.eq(cursorInstant)
                .and(backup.id.gt(request.getIdAfter())));
      }
    }

    if (request.getSortField() == BackupSortField.status && cursor != null) {
      // status는 문자열(EnumType.STRING)로 저장되므로 문자열 비교 사용
      if (isDesc) {
        return backup.backupStatus.stringValue().lt(cursor)
            .or(backup.backupStatus.stringValue().eq(cursor)
                .and(backup.id.lt(request.getIdAfter())));
      } else {
        return backup.backupStatus.stringValue().gt(cursor)
            .or(backup.backupStatus.stringValue().eq(cursor)
                .and(backup.id.gt(request.getIdAfter())));
      }
    }

    // cursor가 없는 경우 id만으로 페이지네이션
    return isDesc
        ? backup.id.lt(request.getIdAfter())
        : backup.id.gt(request.getIdAfter());
  }

  private OrderSpecifier<?> getOrder(BackupListRequest request) {

    boolean isDesc = request.getSortDirection() == Sort.Direction.DESC;

    return switch (request.getSortField()) {

      case startedAt ->
          isDesc ? backup.startedAt.desc() : backup.startedAt.asc();

      case endedAt ->
          isDesc ? backup.endedAt.desc() : backup.endedAt.asc();

      case status ->
          isDesc ? backup.backupStatus.desc() : backup.backupStatus.asc();
    };
  }
}