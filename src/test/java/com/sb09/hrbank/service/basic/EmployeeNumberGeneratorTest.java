package com.sb09.hrbank.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.sb09.hrbank.entity.Employee;
import com.sb09.hrbank.repository.EmployeeRepository;
import com.sb09.hrbank.service.basic.EmployeeNumberGenerator;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeNumberGeneratorTest {

  @Mock
  private EmployeeRepository employeeRepository;

  @InjectMocks
  private EmployeeNumberGenerator employeeNumberGenerator;

  @Test
  void generate_returnsFirstSequence_whenNoEmployeeNumberExistsForYear() {
    given(employeeRepository.findTopByEmployeeNumberStartingWithOrderByEmployeeNumberDesc("EMP-2023-"))
        .willReturn(Optional.empty());
    given(employeeRepository.existsByEmployeeNumber("EMP-2023-001")).willReturn(false);

    String employeeNumber = employeeNumberGenerator.generate(LocalDate.of(2023, 1, 1));

    assertThat(employeeNumber).isEqualTo("EMP-2023-001");
  }

  @Test
  void generate_skipsAlreadyExistingCandidate_whenCollisionIsDetected() {
    Employee latest = Employee.create(
        LocalDate.of(2023, 1, 1),
        "Existing",
        "existing@example.com",
        "EMP-2023-001",
        "Backend Engineer",
        1L,
        null
    );

    given(employeeRepository.findTopByEmployeeNumberStartingWithOrderByEmployeeNumberDesc("EMP-2023-"))
        .willReturn(Optional.of(latest));
    given(employeeRepository.existsByEmployeeNumber("EMP-2023-002"))
        .willReturn(true);
    given(employeeRepository.existsByEmployeeNumber("EMP-2023-003"))
        .willReturn(false);

    String employeeNumber = employeeNumberGenerator.generate(LocalDate.of(2023, 1, 1));

    assertThat(employeeNumber).isEqualTo("EMP-2023-003");
  }
}

