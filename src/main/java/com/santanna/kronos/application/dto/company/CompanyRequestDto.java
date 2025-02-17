package com.santanna.kronos.application.dto.company;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompanyRequestDto(
        @Schema(description = "Identificador único da empresa", example = "1234567890112")
        @NotBlank(message = "CNPJ " + NOT_BLANK)
        @Size(min = 13, max = 13, message = CHARACTER_SIZE)
        String cnpj,

        @Schema(description = "Primeiro nome do colaborador", example = "João")
        @NotBlank(message = "Nome " + NOT_BLANK)
        @Size(min = 1, max = 100)
        String nameCompany
) {
    public static final String NOT_BLANK = "Deve ser preenchido";
    public static final String CHARACTER_SIZE = "Deve conter exatamente 13 caractetes";
}
