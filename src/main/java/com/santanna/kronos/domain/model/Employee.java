package com.santanna.kronos.domain.model;

import lombok.*;

import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Employee {
    private UUID idEmployee;
    private String cpf;
    private String name;
    private String surname;
    private String email;
    private Double salary;
    private String position;
}
