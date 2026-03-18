package com.sb09.hrbank.repository.basic;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sb09.hrbank.dto.request.ChangeLogListRequest;
import com.sb09.hrbank.entity.ChangeLog;
import com.sb09.hrbank.entity.ChangeType;
import com.sb09.hrbank.entity.QChangeLog;
import com.sb09.hrbank.repository.ChangeLogRepositoryCustom;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChangeLogRepositoryImpl implements ChangeLogRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  private final QChangeLog changeLog = QChangeLog.changeLog;

  @Override
  public Slice<ChangeLog> searchChangeLogs(ChangeLogListRequest request) {

    OrderSpecifier<?> order = getOrder(request);
    boolean isDesc = request.getSortDirection() == Sort.Direction.DESC;
    List<ChangeLog> results = queryFactory
        .selectFrom(changeLog)
        .where(
            employeeNumberContains(request.getEmployeeNumber()),
            typeEq(request.getType()),
            memoContains(request.getMemo()),
            ipAddressContains(request.getIpAddress()),
            atGoe(request.getAtFrom()),
            atLoe(request.getAtTo()),
            cursorCondition(request)
        )
        .orderBy(order, isDesc ? changeLog.id.desc() : changeLog.id.asc())
        .limit(request.getSize() + 1)
        .fetch();

    boolean hasNext = results.size() > request.getSize();

    if(hasNext){
      results.remove(request.getSize());
    }

    return new SliceImpl<>(
        results,
        PageRequest.of(0, request.getSize()),
        hasNext
    );
  }

  private BooleanExpression employeeNumberContains(String number){
    if(number == null || number.isBlank()){
      return null;
    }
    return changeLog.employeeNumber.contains(number);
  }

  private BooleanExpression typeEq(ChangeType type){
    if(type == null || type == ChangeType.ALL){
      return null;
    }
    return changeLog.type.eq(type);
  }

  private BooleanExpression memoContains(String memo){
    if(memo == null || memo.isBlank()){
      return null;
    }
    return changeLog.memo.contains(memo);
  }
  private BooleanExpression ipAddressContains(String ip){
    if(ip == null || ip.isBlank()){
      return null;
    }
    return changeLog.ipAddress.contains(ip);
  }

  private BooleanExpression atGoe(Instant from) {
    if (from == null) {
      return null;
    }
    return changeLog.createdAt.goe(from);
  }

  private BooleanExpression atLoe(Instant to) {
    if (to == null) {
      return null;
    }
    return changeLog.createdAt.loe(to);
  }

  private BooleanExpression cursorCondition(ChangeLogListRequest request) {

    if (request.getCursor() == null || request.getIdAfter() == null) {
      return null;
    }

    boolean isDesc = request.getSortDirection() == Sort.Direction.DESC;
    return switch (request.getSortField()) {
      case at -> {
        Instant cursor = Instant.parse(request.getCursor());
        yield isDesc
            ? changeLog.createdAt.lt(cursor).or(changeLog.createdAt.eq(cursor).and(changeLog.id.lt(request.getIdAfter())))
            : changeLog.createdAt.gt(cursor).or(changeLog.createdAt.eq(cursor).and(changeLog.id.gt(request.getIdAfter())));
      }
      case ipAddress -> {
        String cursor = request.getCursor();
        yield isDesc
            ? changeLog.ipAddress.lt(cursor).or(changeLog.ipAddress.eq(cursor).and(changeLog.id.lt(request.getIdAfter())))
            : changeLog.ipAddress.gt(cursor).or(changeLog.ipAddress.eq(cursor).and(changeLog.id.gt(request.getIdAfter())));
      }
    };
  }

  private OrderSpecifier<?> getOrder(ChangeLogListRequest request) {

    boolean isDesc = request.getSortDirection() == Sort.Direction.DESC;

    return switch (request.getSortField()) {
      case at -> isDesc ? changeLog.createdAt.desc() : changeLog.createdAt.asc();

      case ipAddress -> isDesc ? changeLog.ipAddress.desc() : changeLog.ipAddress.asc();
    };
  }
}
