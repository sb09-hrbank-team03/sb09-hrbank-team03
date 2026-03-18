package com.sb09.hrbank.service.basic;

import com.sb09.hrbank.dto.request.DepartmentUpdateRequest;
import com.sb09.hrbank.repository.EmployeeRepository;
import java.util.NoSuchElementException;
import org.springframework.transaction.annotation.Transactional;
import com.sb09.hrbank.dto.response.DepartmentDto;
import com.sb09.hrbank.dto.request.DepartmentCreateRequest;
import com.sb09.hrbank.entity.Department;
import com.sb09.hrbank.mapper.DepartmentMapper;
import com.sb09.hrbank.repository.DepartmentRepository;
import com.sb09.hrbank.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sb09.hrbank.dto.common.CursorPageResponse;
import com.sb09.hrbank.dto.request.DepartmentSearchRequest;
import com.sb09.hrbank.dto.request.DepartmentSortField;
import com.sb09.hrbank.mapper.CursorPageResponseMapper;
import org.springframework.data.domain.Slice;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicDepartmentService implements DepartmentService {

  private final EmployeeRepository employeeRepository;
  private final DepartmentRepository departmentRepository;
  private final DepartmentMapper departmentMapper;
  private final CursorPageResponseMapper cursorPageResponseMapper;

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  @Transactional
  public DepartmentDto create(DepartmentCreateRequest request) {

    boolean isDuplicate = departmentRepository.existsByName(request.name());

    if (isDuplicate) {
      throw new IllegalArgumentException("이미 존재하는 부서 이름입니다.");
    }

    Department newDepartment = departmentMapper.toEntity(request);
    Department savedDepartment = departmentRepository.save(newDepartment);

    return departmentMapper.toDto(savedDepartment, 0L);
  }

  @Override
  @Transactional
  public DepartmentDto update(Long id, DepartmentUpdateRequest request) {

    Department department = departmentRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부서입니다."));

    boolean isDuplicate = departmentRepository.existsByNameAndIdNot(request.name(), id);
    if (isDuplicate) {
      throw new IllegalArgumentException("이미 존재하는 부서 이름입니다.");
    }

    department.update(request.name(), request.description(), request.establishedDate());

    Long employeeCount = employeeRepository.countByDepartmentId(department.getId());
    return departmentMapper.toDto(department, employeeCount);
  }

  @Override
  @Transactional(readOnly = true)
  public CursorPageResponse<DepartmentDto> searchDepartments(DepartmentSearchRequest request) {

    Slice<Department> departmentSlice = departmentRepository.searchDepartments(request);

    List<Department> departments = departmentSlice.getContent();
    Set<Long> departmentIds = departments.stream()
        .map(Department::getId)
        .collect(Collectors.toSet());
    Map<Long, Long> employeeCountMap = getEmployeeCountsByDepartmentIds(departmentIds);

    Long totalElements = departmentRepository.countDepartments(request);

    return cursorPageResponseMapper.fromSlice(
        departmentSlice,
        department -> {
          Long employeeCount = employeeCountMap.getOrDefault(department.getId(), 0L);
          return departmentMapper.toDto(department, employeeCount);
        },
        department -> {
          if (request.getSortField() != null && request.getSortField() == DepartmentSortField.name) {
            return department.getName();
          }
          return department.getEstablishedDate().toString();
        },
        Department::getId,
        totalElements
    );
  }

  private Map<Long, Long> getEmployeeCountsByDepartmentIds(Collection<Long> departmentIds) {
    if (departmentIds == null || departmentIds.isEmpty()) {
      return Collections.emptyMap();
    }
    List<Object[]> results = entityManager.createQuery(
            "SELECT e.department.id, COUNT(e) FROM Employee e " +
                "WHERE e.department.id IN :departmentIds " +
                "GROUP BY e.department.id",
            Object[].class)
        .setParameter("departmentIds", departmentIds)
        .getResultList();
    Map<Long, Long> employeeCountMap = new HashMap<>();
    for (Object[] row : results) {
      Long departmentId = (Long) row[0];
      Long count = (Long) row[1];
      employeeCountMap.put(departmentId, count != null ? count : 0L);
    }
    return employeeCountMap;
  }

  @Override
  @Transactional(readOnly = true)
  public DepartmentDto findById(Long id) {
    Department department = departmentRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 부서입니다."));

    Long employeeCount = employeeRepository.countByDepartmentId(department.getId());
    return departmentMapper.toDto(department, employeeCount);
  }

  @Override
  @Transactional
  public void delete(Long id) {
    Department department = departmentRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 부서입니다."));

    if (employeeRepository.existsByDepartmentId(id)) {
      throw new IllegalArgumentException("소속 직원이 있는 부서는 삭제할 수 없습니다.");
    }
    departmentRepository.delete(department);
  }
}
