package com.santanna.kronos.infrastructure.mapper;

import com.santanna.kronos.domain.model.Company;
import com.santanna.kronos.domain.model.Employee;
import com.santanna.kronos.infrastructure.entity.CompanyEntity;
import com.santanna.kronos.infrastructure.entity.EmployeeEntity;

import java.util.List;
import java.util.stream.Collectors;

public class ConverterDomainEntity {
    // Conversão de CompanyEntity para Company (domínio)
    public static Company toDomain(CompanyEntity entity) {
        List<Employee> employees = null;
        if (entity.getEmployees() != null) {
            employees = entity.getEmployees().stream()
                    .map(ConverterDomainEntity::toDomain)
                    .collect(Collectors.toList());
        }
        return Company.builder()
                .id(entity.getId())
                .nameCompany(entity.getNameCompany())
                .cnpj(entity.getCnpj())
                .employees(employees)
                .build();
    }

    // Conversão de Company (domínio) para CompanyEntity
    public static CompanyEntity toEntity(Company domain) {
        CompanyEntity companyEntity = CompanyEntity.builder()
                .id(domain.getId())
                .nameCompany(domain.getNameCompany())
                .cnpj(domain.getCnpj())
                .build();
        if (domain.getEmployees() != null) {
            List<EmployeeEntity> employeeEntities = domain.getEmployees().stream()
                    .map(emp -> {
                        EmployeeEntity employeeEntity = toEntity(emp);
                        // Define a relação de volta para evitar inconsistência
                        employeeEntity.setCompany(companyEntity);
                        return employeeEntity;
                    })
                    .collect(Collectors.toList());
            companyEntity.setEmployees(employeeEntities);
        }
        return companyEntity;
    }

    // Conversão de EmployeeEntity para Employee (domínio)
    public static Employee toDomain(EmployeeEntity entity) {
        // Para evitar recursão infinita, você pode mapear a Company de forma rasa (por exemplo, sem os funcionários)
        Company company = null;
        if (entity.getCompany() != null) {
            company = Company.builder()
                    .id(entity.getCompany().getId())
                    .nameCompany(entity.getCompany().getNameCompany())
                    .cnpj(entity.getCompany().getCnpj())
                    .build();
        }
        return Employee.builder()
                .idEmployee(entity.getIdEmployee())
                .cpf(entity.getCpf())
                .name(entity.getName())
                .surname(entity.getSurname())
                .email(entity.getEmail())
                .salary(entity.getSalary())
                .position(entity.getPosition())
                .company(company)
                .build();
    }

    // Conversão de Employee (domínio) para EmployeeEntity
    public static EmployeeEntity toEntity(Employee domain) {
        EmployeeEntity employeeEntity = EmployeeEntity.builder()
                .idEmployee(domain.getIdEmployee())
                .cpf(domain.getCpf())
                .name(domain.getName())
                .surname(domain.getSurname())
                .email(domain.getEmail())
                .salary(domain.getSalary())
                .position(domain.getPosition())
                .build();
        if (domain.getCompany() != null) {
            // Pode ser feito um mapeamento superficial, se a Company já estiver persistida
            CompanyEntity companyEntity = CompanyEntity.builder()
                    .id(domain.getCompany().getId())
                    .nameCompany(domain.getCompany().getNameCompany())
                    .cnpj(domain.getCompany().getCnpj())
                    .build();
            employeeEntity.setCompany(companyEntity);
        }
        return employeeEntity;
    }
}
