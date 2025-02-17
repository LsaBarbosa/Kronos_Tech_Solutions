package com.santanna.kronos.application.usecase;

import com.santanna.kronos.application.dto.employee.EmployeeRequestDto;
import com.santanna.kronos.application.dto.employee.EmployeeResponseDto;
import com.santanna.kronos.application.dto.employee.UpdateRequestDto;
import com.santanna.kronos.application.exception.BadRequestException;
import com.santanna.kronos.application.exception.NotFoundException;
import com.santanna.kronos.domain.common.PaginatedList;
import com.santanna.kronos.domain.model.Employee;
import com.santanna.kronos.domain.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeUseCaseTest {

    public static final String CPF = "12345678901";
    public static final String NAME = "João";
    public static final String SURNAME = "Silva";
    public static final String MAIL = "joao@example.com";
    public static final double SALARY = 2000.0;
    public static final String DEVELOPER = "Developer";
    public static final String EMPLOYEE_NOT_FOUND_404 = "Colaborador não encontrado";
    public static final String EMPLOYEE_ALREADY_EXIST_400 = "Colaborador/CPF já cadastrado no sistema";

    @Mock
    private EmployeeRepository employeeRepository;
    @InjectMocks
    private EmployeeUseCase employeeUseCase;
    private Employee employee;
    private EmployeeRequestDto employeeRequestDto;
    private UpdateRequestDto updateEmployee;

    @BeforeEach
    void setUp() {
        employeeUseCase = new EmployeeUseCase(employeeRepository);
        UUID id = UUID.randomUUID();
        employee = Employee.builder()
                .idEmployee(id)
                .cpf(CPF)
                .name(NAME)
                .surname(SURNAME)
                .email(MAIL)
                .salary(SALARY)
                .position(DEVELOPER)
                .build();

        employeeRequestDto = new EmployeeRequestDto(
                CPF,
                NAME,
                SURNAME,
                MAIL,
                SALARY,
                DEVELOPER
        );
        updateEmployee = new UpdateRequestDto(
                CPF,
                NAME,
                SURNAME,
                MAIL,
                SALARY,
                DEVELOPER
        );
    }

    @Test
    void shouldReturnEmployeeById_Success() {
        when(employeeRepository.findEmployee(employee.getIdEmployee())).thenReturn(Optional.of(employee));

        EmployeeResponseDto response = employeeUseCase.getEmployeeById(employee.getIdEmployee());

        assertNotNull(response);
        assertEquals(employee.getIdEmployee(), response.id());
        assertEquals(employee.getName(), response.name());
        assertEquals(employee.getSurname(), response.surname());
        assertEquals(employee.getEmail(), response.email());
        assertEquals(employee.getSalary(), response.salary());
        assertEquals(employee.getPosition(), response.position());
    }

    @Test
    void shouldReturnAllEmployees_Success() {
        UUID id2 = UUID.randomUUID();
        Employee employee2 = Employee.builder()
                .idEmployee(id2)
                .cpf("12345678900")
                .name("NAME")
                .surname("SURNAME")
                .email("MAIL@EXEMPLE.com")
                .salary(9999.0)
                .position("DEVELOPER")
                .build();
        List<Employee> employees = Arrays.asList(employee, employee2);

        PaginatedList<Employee> employeePaginated = new PaginatedList<>(
                employees, 0, 2, 2L
        );

        when(employeeRepository.findAllEmployees(0, 2)).thenReturn(employeePaginated);
        PaginatedList<EmployeeResponseDto> response = employeeUseCase.getAllEmployees(0, 2);

        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals(0, response.getPageNumber());
        assertEquals(2, response.getPageSize());
        assertEquals(2L, response.getTotalElements());
        verify(employeeRepository, times(1)).findAllEmployees(0, 2);
    }

    @Test
    void shouldCreateEmployee_Success() {
        when(employeeRepository.findCpf(employeeRequestDto.cpf())).thenReturn(Optional.empty());
        employeeUseCase.addEmployee(employeeRequestDto);

        ArgumentCaptor<Employee> employeeCaptor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository, times(1)).saveEmployee(employeeCaptor.capture());
        var savedEmployee = employeeCaptor.getValue();

        assertEquals(employeeRequestDto.name(), savedEmployee.getName());
        assertEquals(employeeRequestDto.surname(), savedEmployee.getSurname());
        assertEquals(employeeRequestDto.email(), savedEmployee.getEmail());
        assertEquals(employeeRequestDto.cpf(), savedEmployee.getCpf());
        assertEquals(employeeRequestDto.salary(), savedEmployee.getSalary());
        assertEquals(employeeRequestDto.position(), savedEmployee.getPosition());
        verify(employeeRepository, times(1)).findCpf(employeeRequestDto.cpf());
    }

    @Test
    void shouldUpdateEmployee_Success() {
        when(employeeRepository.findEmployee(employee.getIdEmployee())).thenReturn(Optional.of(employee));
        employeeUseCase.updateEmployee(employee.getIdEmployee(), updateEmployee);

        ArgumentCaptor<Employee> employeeCaptor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository, times(1)).saveEmployee(employeeCaptor.capture());
        var updatedEmployee = employeeCaptor.getValue();

        verify(employeeRepository, times(1)).findEmployee(employee.getIdEmployee());
        assertDoesNotThrow(() -> employeeUseCase.updateEmployee(employee.getIdEmployee(), updateEmployee));
        assertEquals(updateEmployee.name(), updatedEmployee.getName());
        assertEquals(updateEmployee.surname(), updatedEmployee.getSurname());
        assertEquals(updateEmployee.email(), updatedEmployee.getEmail());
        assertEquals(updateEmployee.cpf(), updatedEmployee.getCpf());
        assertEquals(updateEmployee.salary(), updatedEmployee.getSalary());
        assertEquals(updateEmployee.position(), updatedEmployee.getPosition());

    }

    @Test
    void shouldUpdateEmail_Success() {
        when(employeeRepository.findEmployee(employee.getIdEmployee())).thenReturn(Optional.of(employee));
        employeeUseCase.updtadeEmail(employee.getIdEmployee(), updateEmployee);

        ArgumentCaptor<Employee> employeeCaptor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository, times(1)).saveEmployee(employeeCaptor.capture());
        var updatedEmployee = employeeCaptor.getValue();

        verify(employeeRepository, times(1)).findEmployee(employee.getIdEmployee());
        assertDoesNotThrow(() -> employeeUseCase.updateEmployee(employee.getIdEmployee(), updateEmployee));
        assertEquals(updateEmployee.email(), updatedEmployee.getEmail());
    }

    @Test
    void shouldDeleteEmployee_Success() {
        when(employeeRepository.findEmployee(employee.getIdEmployee())).thenReturn(Optional.of(employee));

        assertDoesNotThrow(() -> employeeUseCase.deleteEmployee(employee.getIdEmployee()));

        verify(employeeRepository, times(1)).findEmployee(employee.getIdEmployee());
        verify(employeeRepository, times(1)).deleteEmployee(employee.getIdEmployee());
        verifyNoMoreInteractions(employeeRepository);
    }

    @Test
    void shouldNotReturnEmployeeById_NotFoundExceptio() {
        when(employeeRepository.findEmployee(employee.getIdEmployee())).thenReturn(Optional.empty());
        var notFoundException = assertThrows(NotFoundException.class,
                () -> employeeUseCase.getEmployeeById(employee.getIdEmployee()));

        assertEquals(EMPLOYEE_NOT_FOUND_404, notFoundException.getMessage());
        verify(employeeRepository, times(1)).findEmployee(employee.getIdEmployee());
    }

    @Test
    void shouldReturnEmployeeAlreadyExists_BadRequestException() {
        when(employeeRepository.findCpf(employee.getCpf())).thenReturn(Optional.of(employee));
        var badRequestException = assertThrows(BadRequestException.class,
                () -> employeeUseCase.addEmployee(employeeRequestDto));
        assertEquals(EMPLOYEE_ALREADY_EXIST_400, badRequestException.getMessage());
        verify(employeeRepository, never()).saveEmployee(employee);
    }

    @Test
    void shouldNotUpdateEmployee_NotFoundExceptio() {
        when(employeeRepository.findEmployee(employee.getIdEmployee())).thenReturn(Optional.empty());
        var notFoundException = assertThrows(NotFoundException.class,
                () -> employeeUseCase.updateEmployee(employee.getIdEmployee(), updateEmployee));
        assertEquals(EMPLOYEE_NOT_FOUND_404, notFoundException.getMessage());
        verify(employeeRepository, times(1)).findEmployee(employee.getIdEmployee());
    }

    @Test
    void shouldNotUpdateEmailEmployee_NotFoundExceptio() {
        when(employeeRepository.findEmployee(employee.getIdEmployee())).thenReturn(Optional.empty());
        var notFoundException = assertThrows(NotFoundException.class,
                () -> employeeUseCase.updtadeEmail(employee.getIdEmployee(), updateEmployee));
        assertEquals(EMPLOYEE_NOT_FOUND_404, notFoundException.getMessage());
        verify(employeeRepository, times(1)).findEmployee(employee.getIdEmployee());
    }

    @Test
    void shouldNotDeleteEmployee_NotFoundException() {
        when(employeeRepository.findEmployee(employee.getIdEmployee())).thenReturn(Optional.empty());

        var notFoundException = assertThrows(NotFoundException.class,
                () -> employeeUseCase.deleteEmployee(employee.getIdEmployee()));

        assertEquals(EMPLOYEE_NOT_FOUND_404, notFoundException.getMessage());
        verify(employeeRepository, times(1)).findEmployee(employee.getIdEmployee());
    }
}