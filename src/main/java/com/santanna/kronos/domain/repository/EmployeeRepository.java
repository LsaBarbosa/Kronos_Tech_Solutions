package com.santanna.kronos.domain.repository;


import com.santanna.kronos.domain.common.PaginatedList;
import com.santanna.kronos.domain.model.Employee;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository {
    Optional<Employee> findEmployee(UUID employeeId);
    Optional<Employee> findCpf(String cpf);
    Employee saveEmployee(Employee employee);
    PaginatedList<Employee> findAllEmployees(int page, int size);
    void deleteEmployee(UUID employeeId);

}
