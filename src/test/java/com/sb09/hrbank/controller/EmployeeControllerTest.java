package com.sb09.hrbank.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sb09.hrbank.dto.response.EmployeeDto;
import com.sb09.hrbank.entity.WorkStatus;
import com.sb09.hrbank.exception.GlobalExceptionHandler;
import com.sb09.hrbank.service.EmployeeService;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

  private MockMvc mockMvc;

  @Mock
  private EmployeeService employeeService;

  @InjectMocks
  private EmployeeController employeeController;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(employeeController)
        .setControllerAdvice(new GlobalExceptionHandler())
        .setMessageConverters(new MappingJackson2HttpMessageConverter())
        .build();
  }

  @Test
  void getById_returnsEmployee_whenEmployeeExists() throws Exception {
    given(employeeService.getById(1L)).willReturn(new EmployeeDto(
        1L,
        "John Doe",
        "john@example.com",
        "EMP-2024-001",
        10L,
        "Platform",
        "Backend Engineer",
        LocalDate.of(2024, 1, 3),
        WorkStatus.ACTIVE,
        null
    ));

    mockMvc.perform(get("/api/employees/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.name").value("John Doe"))
        .andExpect(jsonPath("$.email").value("john@example.com"))
        .andExpect(jsonPath("$.employeeNumber").value("EMP-2024-001"))
        .andExpect(jsonPath("$.departmentName").value("Platform"));
  }

  @Test
  void getById_returns404_whenEmployeeDoesNotExist() throws Exception {
    given(employeeService.getById(99L))
        .willThrow(new NoSuchElementException("Employee not found. id=99"));

    mockMvc.perform(get("/api/employees/99"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.details").value("Employee not found. id=99"));
  }
}


