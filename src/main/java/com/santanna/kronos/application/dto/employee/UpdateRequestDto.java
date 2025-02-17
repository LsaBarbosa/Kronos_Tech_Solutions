package com.santanna.kronos.application.dto.employee;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateRequestDto(
        @Schema(description = "Identificador único", example = "12345678901")
        String cpf,
        @Schema(description = "Primeiro nome do colaborador", example = "João")
        String name,
        @Schema(description = "Sobrenome nome do colaborador", example = "Da Silva")
        String surname,
        @Schema(description = "Email do colaborador", example = "meu-email@exemplo.com")
        String email,
        @Schema(description = "Sálario do colaborador", example = "1200")
        Double salary,
        @Schema(description = "Cargo do colaborador", example = "Engenheiro")
        String position
) {

}
