package com.santanna.kronos.adapters.controller;

import com.santanna.kronos.application.dto.company.CompanyRequestDto;
import com.santanna.kronos.application.dto.company.CompanyResponseDto;
import com.santanna.kronos.application.dto.company.UpdateCompanyRequestDto;
import com.santanna.kronos.application.usecase.CompanyUseCase;
import com.santanna.kronos.domain.common.PaginatedList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Company Controller", description = "Management Company")
@RestController
@RequestMapping("/v1/company")
public class CompanyController {
    private final CompanyUseCase companyUseCase;

    public CompanyController(CompanyUseCase companyUseCase) {
        this.companyUseCase = companyUseCase;
    }

    @Operation(
            summary = "Buscar Empresa pelo id",
            description = "Acesso as informações de uma empresa."
    )
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CompanyResponseDto> getCompanyById(@PathVariable UUID id) {
        var companyData = companyUseCase.findCompanyById(id);
        return ResponseEntity.ok(companyData);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Administrador busca todos as empresas",
            description = "Retorna uma lista com dadps de todas as empresas."
    )
    public ResponseEntity<PaginatedList<CompanyResponseDto>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PaginatedList<CompanyResponseDto> employees = companyUseCase.findAllCompanies(page, size);
        return ResponseEntity.ok(employees);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Administrador registra uma nova empresa",
            description = " Cadastra uma empresa."
    )
    public ResponseEntity<Void> addCompany(@Valid @RequestBody CompanyRequestDto addCompanyDto) {
        companyUseCase.addCompany(addCompanyDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{idTarget}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Administrador atualiza dados da empresa",
            description = "Atualiza dados de uma empresa."
    )
    public ResponseEntity<Void> updateCompany(
            @PathVariable UUID idTarget,
            @Valid @RequestBody UpdateCompanyRequestDto updateCompanyDto) {
        companyUseCase.updateCompany(idTarget, updateCompanyDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{idTarget}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Administrador Deleta uma Empresa")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID idTarget) {
        companyUseCase.deleteCompany(idTarget);
        return ResponseEntity.noContent().build();
    }
}
