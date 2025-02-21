package com.santanna.kronos.application.utils;

import com.santanna.kronos.application.dto.company.CompanyResponseDto;
import com.santanna.kronos.application.dto.employee.EmployeeResponseDto;
import com.santanna.kronos.domain.model.Company;
import com.santanna.kronos.domain.model.Employee;

public class ConverterDto {

    public static EmployeeResponseDto toDto(Employee employee) {
        String companyName = employee.getCompany() != null ? employee.getCompany().getNameCompany() : null;
        return new EmployeeResponseDto(
                employee.getIdEmployee(),
                employee.getCpf(),
                employee.getName(),
                employee.getSurname(),
                employee.getEmail(),
                employee.getSalary(),
                employee.getPosition(),
                companyName
        );
    }

    public static CompanyResponseDto toDto(Company company) {
        var count = company.getEmployees() != null ? company.getEmployees().size() : 0;

        return new CompanyResponseDto(
                company.getId(),
                company.getCnpj(),
                company.getNameCompany(),
                count
        );
    }
}
