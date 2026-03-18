package com.sb09.hrbank.repository.basic;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sb09.hrbank.dto.request.DepartmentSearchRequest;
import com.sb09.hrbank.dto.request.DepartmentSortField;
import com.sb09.hrbank.entity.Department;
import com.sb09.hrbank.entity.QDepartment;
import com.sb09.hrbank.repository.DepartmentRepositoryCustom;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DepartmentRepositoryImpl implements DepartmentRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  private final QDepartment department = QDepartment.department;

  @Override
  public Slice<Department> searchDepartments(DepartmentSearchRequest request) {
    int size = request.getSize();

    List<Department> results = queryFactory
        .selectFrom(department)
        .where(
            keywordContains(request.getNameOrDescription()),
            cursorCondition(request)
        )
        .orderBy(primaryOrder(request), idOrder(request))
        .limit(size + 1L)
        .fetch();

    boolean hasNext = results.size() > size;
    if (hasNext) {
      results.remove(size);
    }
    return new SliceImpl<>(results, PageRequest.of(0, size), hasNext);
  }

  private BooleanExpression keywordContains(String keyword) {
    if (keyword == null || keyword.isBlank()) {
      return null;
    }
    return department.name.containsIgnoreCase(keyword)
        .or(department.description.containsIgnoreCase(keyword));
  }

  private BooleanExpression cursorCondition(DepartmentSearchRequest request) {
    if (request.getCursor() == null || request.getLastElementId() == null) {
      return null;
    }
    Sort.Direction sortDirection =
        request.getSortDirection() == null ? Sort.Direction.DESC : request.getSortDirection();
    DepartmentSortField sortField =
        request.getSortBy() == null ? DepartmentSortField.establishedDate : request.getSortBy();

    return switch (sortField) {
      case name ->
          compareWithTieBreaker(department.name, request.getCursor(), request.getLastElementId(),
              sortDirection);
      case establishedDate -> {
        try {
          LocalDate cursorDate = LocalDate.parse(request.getCursor());
          yield compareWithTieBreaker(department.establishedDate, cursorDate,
              request.getLastElementId(), sortDirection);
        } catch (DateTimeParseException e) {
          throw new IllegalArgumentException("설립일 커서 형식이 올바르지 않습니다. cursor=" +
              request.getCursor(), e);
        }
      }
    };
  }

  private <T extends Comparable<? super T>> BooleanExpression compareWithTieBreaker(
      ComparableExpression<T> field, T cursor, Long lastElementId, Sort.Direction direction) {
    if (direction == Sort.Direction.DESC) {
      return field.lt(cursor).or(field.eq(cursor).and(department.id.lt(lastElementId)));
    }
    return field.gt(cursor).or(field.eq(cursor).and(department.id.gt(lastElementId)));
  }

  private OrderSpecifier<?> primaryOrder(DepartmentSearchRequest request) {
    boolean isDesc =
        (request.getSortDirection() == null ? Sort.Direction.DESC : request.getSortDirection())
            == Sort.Direction.DESC;
    DepartmentSortField sortField =
        request.getSortBy() == null ? DepartmentSortField.establishedDate : request.getSortBy();
    return switch (sortField) {
      case name -> isDesc ? department.name.desc() : department.name.asc();
      case establishedDate ->
          isDesc ? department.establishedDate.desc() : department.establishedDate.asc();
    };
  }

  private OrderSpecifier<Long> idOrder(DepartmentSearchRequest request) {
    boolean isDesc =
        (request.getSortDirection() == null ? Sort.Direction.DESC : request.getSortDirection())
            == Sort.Direction.DESC;
    return isDesc ? department.id.desc() : department.id.asc();
  }
}