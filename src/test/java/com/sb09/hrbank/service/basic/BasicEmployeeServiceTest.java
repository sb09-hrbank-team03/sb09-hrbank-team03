package com.sb09.hrbank.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.sb09.hrbank.dto.request.EmployeeCreateRequest;
import com.sb09.hrbank.dto.response.EmployeeDto;
import com.sb09.hrbank.entity.Employee;
import com.sb09.hrbank.entity.WorkStatus;
import com.sb09.hrbank.repository.DepartmentRepository;
import com.sb09.hrbank.repository.EmployeeQueryRepository;
import com.sb09.hrbank.repository.EmployeeRepository;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BasicEmployeeServiceTest {

  @Mock
  private DepartmentRepository departmentRepository;

  @Mock
  private EmployeeQueryRepository employeeQueryRepository;

  @Mock
  private EmployeeRepository employeeRepository;

  @Mock
  private EmployeeNumberGenerator employeeNumberGenerator;

  @InjectMocks
  private BasicEmployeeService employeeService;

  @Test
  void create_returnsEmployeeDto_whenRequestIsValid() {
    EmployeeCreateRequest request = new EmployeeCreateRequest(
        "John Doe",
        "john@example.com",
        1L,
        "Backend Engineer",
        LocalDate.of(2023, 1, 1),
        null
    );
    Employee saved = Employee.create(
        request.hireDate(),
        request.name(),
        request.email(),
        "EMP-2023-001",
        request.position(),
        request.departmentId(),
        null
    );
    ReflectionTestUtils.setField(saved, "id", 1L);

    given(employeeRepository.existsByEmail("john@example.com")).willReturn(false);
    given(departmentRepository.existsById(1L)).willReturn(true);
    given(employeeNumberGenerator.generate(request.hireDate())).willReturn("EMP-2023-001");
    given(employeeRepository.save(any(Employee.class))).willReturn(saved);
    given(employeeQueryRepository.findEmployeeDtoById(1L)).willReturn(Optional.of(new EmployeeDto(
        1L,
        "John Doe",
        "john@example.com",
        "EMP-2023-001",
        1L,
        "Platform",
        "Backend Engineer",
        LocalDate.of(2023, 1, 1),
        WorkStatus.ACTIVE,
        null
    )));

    EmployeeDto result = employeeService.create(request);

    assertThat(result.employeeNumber()).isEqualTo("EMP-2023-001");
    assertThat(result.departmentName()).isEqualTo("Platform");
    verify(employeeNumberGenerator).generate(request.hireDate());
  }

  @Test
  void create_retriesWhenSaveFailsWithDuplicateEmployeeNumber() {
    EmployeeCreateRequest request = new EmployeeCreateRequest(
        "Jane Doe",
        "jane@example.com",
        1L,
        "Backend Engineer",
        LocalDate.of(2023, 1, 1),
        null
    );
    Employee retriedSave = Employee.create(
        request.hireDate(),
        request.name(),
        request.email(),
        "EMP-2023-002",
        request.position(),
        request.departmentId(),
        null
    );
    ReflectionTestUtils.setField(retriedSave, "id", 2L);

    given(employeeRepository.existsByEmail("jane@example.com")).willReturn(false);
    given(departmentRepository.existsById(1L)).willReturn(true);
    given(employeeNumberGenerator.generate(request.hireDate()))
        .willReturn("EMP-2023-001", "EMP-2023-002");
    given(employeeRepository.save(any(Employee.class)))
        .willThrow(new DataIntegrityViolationException("duplicate employee number"))
        .willReturn(retriedSave);
    given(employeeQueryRepository.findEmployeeDtoById(2L)).willReturn(Optional.of(new EmployeeDto(
        2L,
        "Jane Doe",
        "jane@example.com",
        "EMP-2023-002",
        1L,
        "Platform",
        "Backend Engineer",
        LocalDate.of(2023, 1, 1),
        WorkStatus.ACTIVE,
        null
    )));

    EmployeeDto result = employeeService.create(request);

    assertThat(result.employeeNumber()).isEqualTo("EMP-2023-002");
    verify(employeeNumberGenerator, times(2)).generate(request.hireDate());
  }

  @Test
  void create_throwsNoSuchElementException_whenDepartmentDoesNotExist() {
    EmployeeCreateRequest request = new EmployeeCreateRequest(
        "John Doe",
        "john@example.com",
        999L,
        "Backend Engineer",
        LocalDate.of(2023, 1, 1),
        null
    );

    given(employeeRepository.existsByEmail("john@example.com")).willReturn(false);
    given(departmentRepository.existsById(999L)).willReturn(false);

    assertThatThrownBy(() -> employeeService.create(request))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("해당 부서를 찾을 수 없습니다. id=999");

    verify(employeeRepository, never()).save(any(Employee.class));
    verify(employeeNumberGenerator, never()).generate(any(LocalDate.class));
  }

  @Test
  void getById_returnsEmployeeDto_whenEmployeeExists() {
    given(employeeQueryRepository.findEmployeeDtoById(1L)).willReturn(Optional.of(new EmployeeDto(
        1L,
        "John Doe",
        "hong@example.com",
        "EMP-2024-001",
        10L,
        "Platform",
        "Backend Engineer",
        LocalDate.of(2024, 1, 3),
        WorkStatus.ACTIVE,
        null
    )));

    EmployeeDto result = employeeService.getById(1L);

    assertThat(result.id()).isEqualTo(1L);
    assertThat(result.name()).isEqualTo("John Doe");
    assertThat(result.email()).isEqualTo("hong@example.com");
    assertThat(result.departmentId()).isEqualTo(10L);
    assertThat(result.departmentName()).isEqualTo("Platform");
  }

  @Test
  void getById_throwsNoSuchElementException_whenEmployeeDoesNotExist() {
    given(employeeQueryRepository.findEmployeeDtoById(99L)).willReturn(Optional.empty());

    assertThatThrownBy(() -> employeeService.getById(99L))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("id=99");
  }
}


