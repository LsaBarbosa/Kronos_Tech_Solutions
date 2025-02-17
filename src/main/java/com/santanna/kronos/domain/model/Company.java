package com.santanna.kronos.domain.model;

import lombok.*;

import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Company {
    UUID id;
    String nameCompany;
    String cnpj;

}
