package com.santanna.kronos.application.dto.company;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateCompanyRequestDto(
        @Schema(description = "Identificador único da empresa", example = "2345678901124")
        String cnpj,

        @Schema(description = "Primeiro nome do colaborador", example = "João")
        String nameCompany
) {
}
