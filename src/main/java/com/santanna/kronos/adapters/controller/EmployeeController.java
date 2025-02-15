package com.santanna.kronos.adapters.controller;

import com.santanna.kronos.application.dto.EmployeeRequestDto;
import com.santanna.kronos.application.dto.EmployeeResponseDto;
import com.santanna.kronos.application.dto.UpdateRequestDto;
import com.santanna.kronos.application.usecase.EmployeeUseCase;
import com.santanna.kronos.domain.common.PaginatedList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.ServiceUnavailableException;
import java.util.UUID;

@Tag(name = "Employee Controller", description = "Management Employee")
@RestController
@RequestMapping("/v1/employee")
public class EmployeeController {

    private final EmployeeUseCase employeeUseCase;

    public EmployeeController(EmployeeUseCase employeeUseCase) {
        this.employeeUseCase = employeeUseCase;
    }

    @Operation(
            summary = "Buscar funcionário pelo id",
            description = "Usuário tem acesso as suas própias informações."
    )
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<EmployeeResponseDto> getEmployeeData(@PathVariable UUID id)
            throws ServiceUnavailableException {
        var employeeData = employeeUseCase.getEmployeeById(id);
        return ResponseEntity.ok(employeeData);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Administrador busca todos os funcionários",
            description = "Retorna uma lista com todos os usuários da empresa."
    )
    public ResponseEntity<PaginatedList<EmployeeResponseDto>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws ServiceUnavailableException {
        PaginatedList<EmployeeResponseDto> employees = employeeUseCase.getAllEmployees(page, size);
        return ResponseEntity.ok(employees);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Administrador registra um funcionário",
            description = """
                    Cadastra um colaborador na empresa.
                     \
                    A empresa em que está o usuário MANAGER é automaticamente atribuida ao novo colaborador.
                    """
    )
    public ResponseEntity<Void> addEmployee(@Valid @RequestBody EmployeeRequestDto addEmployeeDto)
            throws ServiceUnavailableException {
        employeeUseCase.addEmployee(addEmployeeDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{idTarget}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Administrador atualiza dados do funcionário",
            description = "Atualiza dados de um colaborador."
    )
    public ResponseEntity<Void> updateEmployee(
            @PathVariable UUID idTarget,
            @Valid @RequestBody UpdateRequestDto updateEmployeeDto) throws ServiceUnavailableException {
        employeeUseCase.updateEmployee(idTarget, updateEmployeeDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("email/update/{idTarget}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Alteração de email",
            description = "Altera o email."
    )
    public ResponseEntity<Void> updateEmail(
            @PathVariable UUID idTarget,
            @Valid @RequestBody UpdateRequestDto updateEmailDto) throws ServiceUnavailableException {
        employeeUseCase.updtadeEmail(idTarget, updateEmailDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{idTarget}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Administrador Deleta funcionário")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID idTarget) throws ServiceUnavailableException {
        employeeUseCase.deleteEmployee(idTarget);
        return ResponseEntity.noContent().build();
    }

}
