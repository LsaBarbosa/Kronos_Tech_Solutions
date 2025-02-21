package com.santanna.kronos.infrastructure.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "tb_employee")
public class EmployeeEntity {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID idEmployee;
    private String cpf;
    private String name;
    private String surname;
    private String email;
    private Double salary;
    private String position;
    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false) // Define a chave estrangeira
    private CompanyEntity company;
}
