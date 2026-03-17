package com.sb09.hrbank.repository.basic;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sb09.hrbank.dto.request.EmployeeSearchRequest;
import com.sb09.hrbank.dto.request.EmployeeSortField;
import com.sb09.hrbank.dto.response.EmployeeDto;
import com.sb09.hrbank.entity.QDepartment;
import com.sb09.hrbank.entity.QEmployee;
import com.sb09.hrbank.repository.EmployeeRepositoryCustom;
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
public class EmployeeRepositoryImpl implements EmployeeRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  private final QEmployee employee = QEmployee.employee;
  private final QDepartment department = QDepartment.department;

  @Override
  public Slice<EmployeeDto> searchEmployees(EmployeeSearchRequest request) {
    int size = request.getSize();

    List<EmployeeDto> results = queryFactory
        .select(
            Projections.constructor(
                EmployeeDto.class,
                employee.id,
                employee.name,
                employee.email,
                employee.employeeNumber,
                department.id,
                department.name,
                employee.position,
                employee.hireDate,
                employee.status,
                employee.profileImageId
            )
        )
        .from(employee)
        .join(employee.department, department)
        .where(
            nameOrEmailContains(request.getNameOrEmail()),
            departmentNameContains(request.getDepartmentName()),
            positionContains(request.getPosition()),
            employeeNumberContains(request.getEmployeeNumber()),
            hireDateFromGoe(request.getHireDateFrom()),
            hireDateToLoe(request.getHireDateTo()),
            statusEq(request),
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

  private BooleanExpression nameOrEmailContains(String nameOrEmail) {
    if (isBlank(nameOrEmail)) {
      return null;
    }

    return employee.name.containsIgnoreCase(nameOrEmail)
        .or(employee.email.containsIgnoreCase(nameOrEmail));
  }

  private BooleanExpression departmentNameContains(String departmentName) {
    if (isBlank(departmentName)) {
      return null;
    }
    return department.name.containsIgnoreCase(departmentName);
  }

  private BooleanExpression positionContains(String position) {
    if (isBlank(position)) {
      return null;
    }
    return employee.position.containsIgnoreCase(position);
  }

  private BooleanExpression employeeNumberContains(String employeeNumber) {
    if (isBlank(employeeNumber)) {
      return null;
    }
    return employee.employeeNumber.containsIgnoreCase(employeeNumber);
  }

  private BooleanExpression hireDateFromGoe(LocalDate hireDateFrom) {
    if (hireDateFrom == null) {
      return null;
    }
    return employee.hireDate.goe(hireDateFrom);
  }

  private BooleanExpression hireDateToLoe(LocalDate hireDateTo) {
    if (hireDateTo == null) {
      return null;
    }
    return employee.hireDate.loe(hireDateTo);
  }

  private BooleanExpression statusEq(EmployeeSearchRequest request) {
    if (request.getStatus() == null) {
      return null;
    }
    return employee.status.eq(request.getStatus());
  }

  private BooleanExpression cursorCondition(EmployeeSearchRequest request) {
    if (request.getCursor() == null || request.getIdAfter() == null) {
      return null;
    }

    Sort.Direction sortDirection = getSortDirection(request);
    EmployeeSortField sortField = getSortField(request);

    return switch (sortField) {
      case name -> compareWithTieBreaker(employee.name, request.getCursor(), request.getIdAfter(),
          sortDirection);
      case employeeNumber -> compareWithTieBreaker(employee.employeeNumber, request.getCursor(),
          request.getIdAfter(), sortDirection);
      case hireDate -> {
        try {
          LocalDate cursorDate = LocalDate.parse(request.getCursor());
          yield compareWithTieBreaker(employee.hireDate, cursorDate,
              request.getIdAfter(), sortDirection);
        } catch (DateTimeParseException e) {
          throw new IllegalArgumentException("입사일 커서 형식이 올바르지 않습니다. cursor=" + request.getCursor(), e);
        }
      }
    };
  }

  private <T extends Comparable<? super T>> BooleanExpression compareWithTieBreaker(
      ComparableExpression<T> field,
      T cursor,
      Long lastElementId,
      Sort.Direction direction
  ) {
    if (direction == Sort.Direction.DESC) {
      return field.lt(cursor)
          .or(field.eq(cursor).and(employee.id.lt(lastElementId)));
    }
    return field.gt(cursor)
        .or(field.eq(cursor).and(employee.id.gt(lastElementId)));
  }

  private OrderSpecifier<?> primaryOrder(EmployeeSearchRequest request) {
    boolean isDesc = getSortDirection(request) == Sort.Direction.DESC;

    return switch (getSortField(request)) {
      case name -> isDesc ? employee.name.desc() : employee.name.asc();
      case employeeNumber -> isDesc ? employee.employeeNumber.desc() : employee.employeeNumber.asc();
      case hireDate -> isDesc ? employee.hireDate.desc() : employee.hireDate.asc();
    };
  }

  private OrderSpecifier<Long> idOrder(EmployeeSearchRequest request) {
    return getSortDirection(request) == Sort.Direction.DESC ? employee.id.desc() : employee.id.asc();
  }

  private EmployeeSortField getSortField(EmployeeSearchRequest request) {
    return request.getSortField() == null ? EmployeeSortField.name : request.getSortField();
  }

  private Sort.Direction getSortDirection(EmployeeSearchRequest request) {
    return request.getSortDirection() == null ? Sort.Direction.DESC : request.getSortDirection();
  }

  private boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}
