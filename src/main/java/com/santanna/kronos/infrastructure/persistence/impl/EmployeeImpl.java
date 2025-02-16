package com.santanna.kronos.infrastructure.persistence.impl;

import com.santanna.kronos.domain.common.PaginatedList;
import com.santanna.kronos.domain.model.Employee;
import com.santanna.kronos.domain.repository.EmployeeRepository;
import com.santanna.kronos.infrastructure.entity.EmployeeEntity;
import com.santanna.kronos.infrastructure.exception.DatabaseException;
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
            return employeePersistence.findById(employeeId).map(this::toDomain);
        } catch (DataAccessException ex) {
            throw new DatabaseException("Error employee ID not found: " + employeeId, ex);
        }
    }

    @Override
    public Optional<Employee> findCpf(String cpf) {
      try {
          return employeePersistence.findEmployeeByCpf(cpf).map(this::toDomain);
      }catch (DatabaseException ex){
          throw new DatabaseException("Error CPF not found: " + cpf, ex);
      }
    }

    @Override
    public PaginatedList<Employee> findAllEmployees(int page, int size) {
        try {
            var entityPage = employeePersistence.findAll(PageRequest.of(page, size));
            return new PaginatedList<>(
                    entityPage.getContent().stream().map(this::toDomain)
                            .collect(Collectors.toList()),
                    entityPage.getNumber(),
                    entityPage.getSize(),
                    entityPage.getTotalElements()
            );
        } catch (DataAccessException ex) {
            throw new DatabaseException("Error employee ID not found", ex);
        }
    }

    @Override
    public Employee saveEmployee(Employee employee) {
        try {
            var employeeEntity = this.toEntity(employee);
            var savedEntity = this.employeePersistence.save(employeeEntity);
            return toDomain(savedEntity);
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

    private Employee toDomain(EmployeeEntity entity) {
        return new Employee(
                entity.getIdEmployee(),
                entity.getCpf(),
                entity.getName(),
                entity.getSurname(),
                entity.getEmail(),
                entity.getSalary(),
                entity.getPosition()
        );
    }

    private EmployeeEntity toEntity(Employee domain) {
        return new EmployeeEntity(
                domain.getIdEmployee(),
                domain.getCpf(),
                domain.getName(),
                domain.getSurname(),
                domain.getEmail(),
                domain.getSalary(),
                domain.getPosition()
        );
    }
}
