package com.santanna.kronos.application.usecase;

import com.santanna.kronos.application.dto.company.CompanyRequestDto;
import com.santanna.kronos.application.dto.company.CompanyResponseDto;
import com.santanna.kronos.application.dto.company.UpdateCompanyRequestDto;
import com.santanna.kronos.application.exception.BadRequestException;
import com.santanna.kronos.application.exception.NotFoundException;
import com.santanna.kronos.application.utils.ConverterDto;
import com.santanna.kronos.domain.common.PaginatedList;
import com.santanna.kronos.domain.model.Company;
import com.santanna.kronos.domain.repository.CompanyRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CompanyUseCase {
    public static final String COMPANY_NOT_FOUND_404 = "Empresa não encontrada";
    public static final String COMPANY_ALREADY_EXIST_400 = "Empresa/CNPJ já cadastrado no sistema";

    private final CompanyRepository companyRepo;

    public CompanyUseCase(CompanyRepository companyRepo) {
        this.companyRepo = companyRepo;
    }

    public CompanyResponseDto findCompanyById(UUID id) {
        var company = companyRepo.findCompany(id)
                .orElseThrow(() -> new NotFoundException(COMPANY_NOT_FOUND_404));
        return ConverterDto.toDto(company);
    }

    public PaginatedList<CompanyResponseDto> findAllCompanies(int page, int size) {
        var companies = companyRepo.findAllCompanies(page, size);
        return new PaginatedList<>(
                companies.getContent().stream().map(ConverterDto::toDto)
                        .collect(Collectors.toList()),
                companies.getPageNumber(),
                companies.getPageSize(),
                companies.getTotalElements()
        );
    }

    @Transactional
    public void addCompany(CompanyRequestDto companyDto) {
        var cnpj = companyRepo.findCnpj(companyDto.cnpj());
        if (cnpj.isPresent()) throw new BadRequestException(COMPANY_ALREADY_EXIST_400);
        var newCompany = creatingCompany(companyDto);
        companyRepo.saveCompany(newCompany);
    }

    @Transactional
    public void updateCompany(UUID id, UpdateCompanyRequestDto companyDto) {
        var idTarget = companyRepo.findCompany(id)
                .orElseThrow(() -> new NotFoundException(COMPANY_NOT_FOUND_404));
        updatingCompany(companyDto, idTarget);
        companyRepo.saveCompany(idTarget);
    }

    @Transactional
    public void deleteCompany(UUID id) {
        var idTarget = companyRepo.findCompany(id)
                .orElseThrow(() -> new NotFoundException(COMPANY_NOT_FOUND_404));
        companyRepo.deleteCompany(idTarget.getId());
    }

    private static Company creatingCompany(CompanyRequestDto companyDto) {
        return Company.builder()
                .nameCompany(companyDto.nameCompany())
                .cnpj(companyDto.cnpj())
                .build();
    }
    private static void updatingCompany(UpdateCompanyRequestDto companyDto, Company idTarget) {
        Optional.ofNullable(companyDto.cnpj()).ifPresent(idTarget::setCnpj);
        Optional.ofNullable(companyDto.nameCompany()).ifPresent(idTarget::setNameCompany);
    }
}
