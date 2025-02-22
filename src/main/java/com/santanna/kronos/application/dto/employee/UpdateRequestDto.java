package com.santanna.kronos.application.dto.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateRequestDto(
        @Schema(description = "Identificador único", example = "12345678901")
        @Size(min = 11, max = 11, message = CHARACTER_SIZE)
        String cpf,
        @Schema(description = "Primeiro nome do colaborador", example = "João")
        String name,
        @Schema(description = "Sobrenome nome do colaborador", example = "Da Silva")
        String surname,
        @Schema(description = "Email do colaborador", example = "meu-email@exemplo.com")
        @Size(min = 1, max = 80)
        @Email(message = CORRECT_FORMAT)
        String email,
        @Schema(description = "Sálario do colaborador", example = "1200")
        Double salary,
        @Schema(description = "Cargo do colaborador", example = "Engenheiro")
        @Positive(message = MUST_BE_POSITIVE)
        String position
) {
        public static final String CHARACTER_SIZE = "Deve conter exatamente 11 caractetes";
        public static final String CORRECT_FORMAT = "Deve estar no formato correto: email@provedor.com";
        public static final String MUST_BE_POSITIVE = "Deve conter valores positivos";
}
