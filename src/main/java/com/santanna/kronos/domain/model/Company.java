package com.santanna.kronos.domain.model;

import com.santanna.kronos.infrastructure.entity.EmployeeEntity;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Company {
    private UUID id;
    private String nameCompany;
    private String cnpj;
    private List<Employee> employees;
}
