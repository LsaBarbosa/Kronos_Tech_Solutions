package com.santanna.kronos.application.dto.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record EmployeeRequestDto(
        @Schema(description = "Identificador único", example = "12345678901")
        @NotBlank(message = "CPF " + NOT_BLANK)
        @Size(min = 11, max = 11, message = CHARACTER_SIZE)
        String cpf,

        @Schema(description = "Primeiro nome do colaborador", example = "João")
        @NotBlank(message = "Nome " + NOT_BLANK)
        @Size(min = 1, max = 100)
        String name,

        @Schema(description = "Sobrenome nome do colaborador", example = "Da Silva")
        @NotBlank(message = "Sobrenome " + NOT_BLANK)
        @Size(min = 1, max = 100)
        String surname,

        @Schema(description = "Email do colaborador", example = "meu-email@exemplo.com")
        @NotBlank(message = "Email " + NOT_BLANK)
        @Size(min = 1, max = 80)
        @Email(message = CORRECT_FORMAT)
        String email,

        @Schema(description = "Sálario do colaborador", example = "1200")
        @NotNull(message = "Salary " + NOT_BLANK)
        @Positive(message = MUST_BE_POSITIVE)
        Double salary,

        @Schema(description = "Cargo do colaborador", example = "Engenheiro")
        @NotBlank(message = "Position " + NOT_BLANK)
        String position
) {
    public static final String NOT_BLANK = "Deve ser preenchido";
    public static final String CHARACTER_SIZE = "Deve conter exatamente 11 caractetes";
    public static final String CORRECT_FORMAT = "Deve estar no formato correto: email@provedor.com";
    public static final String MUST_BE_POSITIVE = "Deve conter valores positivos";
}
