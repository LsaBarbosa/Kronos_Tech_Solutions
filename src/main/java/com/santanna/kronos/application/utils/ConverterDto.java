package com.santanna.kronos.application.utils;

import com.santanna.kronos.application.dto.EmployeeResponseDto;
import com.santanna.kronos.domain.model.Employee;

public class ConverterDto {

    public static EmployeeResponseDto toDto(Employee employee) {
        return new EmployeeResponseDto(
                employee.getIdEmployee(),
                employee.getCpf(),
                employee.getName(),
                employee.getSurname(),
                employee.getEmail(),
                employee.getSalary(),
                employee.getPosition()
        );
    }
}
