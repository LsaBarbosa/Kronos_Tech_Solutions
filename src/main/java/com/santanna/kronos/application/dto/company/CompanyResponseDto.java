package com.santanna.kronos.application.dto.company;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record CompanyResponseDto(
        @Schema(description = "Identificador único do banco")
        UUID id,
        @Schema(description = "Identificador único da Empresa", example = "1234567890123")
        String cnpj,
        @Schema(description = "Primeiro nome do colaborador", example = "Minha Empresa ")
        String nameCompany,
        @Schema(description = "Quantidade de funcionários na empresa", example = "10")
        Integer employeeCount
) {
}
