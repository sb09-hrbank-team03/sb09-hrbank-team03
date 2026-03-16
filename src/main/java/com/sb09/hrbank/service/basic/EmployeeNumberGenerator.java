package com.sb09.hrbank.service.basic;

import com.sb09.hrbank.entity.Employee;
import com.sb09.hrbank.repository.EmployeeRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployeeNumberGenerator {

  private final EmployeeRepository employeeRepository;

  public String generate(LocalDate hireDate) {
    String prefix = "EMP-" + hireDate.getYear() + "-";

    int sequence = employeeRepository
        .findTopByEmployeeNumberStartingWithOrderByEmployeeNumberDesc(prefix)
        .map(Employee::getEmployeeNumber)
        .map(this::extractSequence)
        .map(value -> value + 1)
        .orElse(1);

    String candidate = prefix + String.format("%03d", sequence);
    while (employeeRepository.existsByEmployeeNumber(candidate)) {
      sequence++;
      candidate = prefix + String.format("%03d", sequence);
    }

    return candidate;
  }

  private int extractSequence(String employeeNumber) {
    int lastHyphenIndex = employeeNumber.lastIndexOf('-');
    if (lastHyphenIndex < 0 || lastHyphenIndex == employeeNumber.length() - 1) {
      throw new IllegalStateException("유효하지 않은 사번 형식입니다: " + employeeNumber);
    }

    return Integer.parseInt(employeeNumber.substring(lastHyphenIndex + 1));
  }
}

