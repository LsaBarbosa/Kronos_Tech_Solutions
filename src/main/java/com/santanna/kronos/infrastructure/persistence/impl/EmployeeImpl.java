package com.santanna.kronos.infrastructure.persistence.impl;

import com.santanna.kronos.domain.common.PaginatedList;
import com.santanna.kronos.domain.model.Employee;
import com.santanna.kronos.domain.repository.EmployeeRepository;
import com.santanna.kronos.infrastructure.exception.DatabaseException;
import com.santanna.kronos.infrastructure.mapper.ConverterDomainEntity;
import com.santanna.kronos.infrastructure.persistence.EmployeePersistence;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class EmployeeImpl implements EmployeeRepository {

    private final EmployeePersistence employeePersistence;

    public EmployeeImpl(EmployeePersistence employeePersistence) {
        this.employeePersistence = employeePersistence;

    }

    @Override
    public Optional<Employee> findEmployee(UUID employeeId) {
        try {
            return employeePersistence.findById(employeeId).map(ConverterDomainEntity::toDomain);
        } catch (DataAccessException ex) {
            throw new DatabaseException("Error employee ID not found: " + employeeId, ex);
        }
    }

    @Override
    public Optional<Employee> findCpf(String cpf) {
        try {
            return employeePersistence.findByCpf(cpf).map(ConverterDomainEntity::toDomain);
        } catch (DatabaseException ex) {
            throw new DatabaseException("Error CPF not found: " + cpf, ex);
        }
    }

    @Override
    public PaginatedList<Employee> findAllEmployees(int page, int size) {
        try {
            var entityPage = employeePersistence.findAll(PageRequest.of(page, size));
            return new PaginatedList<>(
                    entityPage.getContent().stream().map(ConverterDomainEntity::toDomain)
                            .collect(Collectors.toList()),
                    entityPage.getNumber(),
                    entityPage.getSize(),
                    entityPage.getTotalElements()
            );
        } catch (DataAccessException ex) {
            throw new DatabaseException("Error! Employees not found", ex);
        }
    }

    @Override
    public Employee saveEmployee(Employee employee) {
        try {
            var employeeEntity = ConverterDomainEntity.toEntity(employee);
            var savedEntity = this.employeePersistence.save(employeeEntity);
            return ConverterDomainEntity.toDomain(savedEntity);
        } catch (DataAccessException ex) {
            throw new DatabaseException("Error saving employee", ex);
        }
    }

    @Override
    public void deleteEmployee(UUID employeeId) {
        try {
            employeePersistence.deleteById(employeeId);
        } catch (DataAccessException ex) {
            throw new DatabaseException("Error deleting employee", ex);
        }
    }

}
