package com.santanna.kronos.application.usecase;

import com.santanna.kronos.application.dto.employee.EmployeeRequestDto;
import com.santanna.kronos.application.dto.employee.EmployeeResponseDto;
import com.santanna.kronos.application.dto.employee.UpdateRequestDto;
import com.santanna.kronos.application.exception.BadRequestException;
import com.santanna.kronos.application.exception.NotFoundException;
import com.santanna.kronos.application.utils.ConverterDto;
import com.santanna.kronos.domain.common.PaginatedList;
import com.santanna.kronos.domain.model.Employee;
import com.santanna.kronos.domain.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmployeeUseCase {
    public static final String EMPLOYEE_NOT_FOUND_404 = "Colaborador não encontrado";
    public static final String EMPLOYEE_ALREADY_EXIST_400 = "Colaborador/CPF já cadastrado no sistema";

    private final EmployeeRepository employeeRepo;

    public EmployeeUseCase(EmployeeRepository employeeRepo) {
        this.employeeRepo = employeeRepo;
    }

    public EmployeeResponseDto getEmployeeById(UUID id) {
            var employee = employeeRepo.findEmployee(id)
                    .orElseThrow(() -> new NotFoundException(EMPLOYEE_NOT_FOUND_404));
            return ConverterDto.toDto(employee);
    }

    public PaginatedList<EmployeeResponseDto> getAllEmployees(int page, int size) {
            var employees = employeeRepo.findAllEmployees(page, size);
            return new PaginatedList<>(
                    employees.getContent().stream().map(ConverterDto::toDto).collect(Collectors.toList()),
                    employees.getPageNumber(),
                    employees.getPageSize(),
                    employees.getTotalElements()
            );
    }

    @Transactional
    public void addEmployee(EmployeeRequestDto addDto) {
            var cpf = employeeRepo.findCpf(addDto.cpf());
            if (cpf.isPresent()) throw new BadRequestException(EMPLOYEE_ALREADY_EXIST_400);

            var newEmployee = creatingEmployee(addDto);
            employeeRepo.saveEmployee(newEmployee);
    }

    @Transactional
    public void updateEmployee(UUID id, UpdateRequestDto updateDto){
            var idTarget = employeeRepo.findEmployee(id)
                    .orElseThrow(() -> new NotFoundException(EMPLOYEE_NOT_FOUND_404));
            updateEmployee(updateDto, idTarget);
            employeeRepo.saveEmployee(idTarget);
    }

    @Transactional
    public void updtadeEmail(UUID id, UpdateRequestDto emailDto){
            var idTarget = employeeRepo.findEmployee(id)
                    .orElseThrow(() -> new NotFoundException(EMPLOYEE_NOT_FOUND_404));

            Optional.ofNullable(emailDto.email()).ifPresent(idTarget::setEmail);

            employeeRepo.saveEmployee(idTarget);
    }

    @Transactional
    public void deleteEmployee(UUID id){
            var idTarget = employeeRepo.findEmployee(id)
                    .orElseThrow(() -> new NotFoundException(EMPLOYEE_NOT_FOUND_404));

            employeeRepo.deleteEmployee(idTarget.getIdEmployee());
    }

    private static void updateEmployee(UpdateRequestDto updateDto, Employee idTarget) {
        Optional.ofNullable(updateDto.email()).ifPresent(idTarget::setEmail);
        Optional.ofNullable(updateDto.name()).ifPresent(idTarget::setName);
        Optional.ofNullable(updateDto.surname()).ifPresent(idTarget::setSurname);
        Optional.ofNullable(updateDto.position()).ifPresent(idTarget::setPosition);
        Optional.ofNullable(updateDto.salary()).ifPresent(idTarget::setSalary);
        Optional.ofNullable(updateDto.cpf()).ifPresent(idTarget::setCpf);
    }

    private static Employee creatingEmployee(EmployeeRequestDto employeeRequestDto) {
        return Employee.builder()
                .name(employeeRequestDto.name())
                .surname(employeeRequestDto.surname())
                .email(employeeRequestDto.email())
                .position(employeeRequestDto.position())
                .salary(employeeRequestDto.salary())
                .cpf(employeeRequestDto.cpf())
                .build();
    }
}
