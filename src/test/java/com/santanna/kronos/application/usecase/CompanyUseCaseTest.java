package com.santanna.kronos.application.usecase;

import com.santanna.kronos.application.dto.company.CompanyRequestDto;
import com.santanna.kronos.application.dto.company.CompanyResponseDto;
import com.santanna.kronos.application.dto.company.UpdateCompanyRequestDto;
import com.santanna.kronos.application.exception.BadRequestException;
import com.santanna.kronos.application.exception.NotFoundException;
import com.santanna.kronos.domain.common.PaginatedList;
import com.santanna.kronos.domain.model.Company;
import com.santanna.kronos.domain.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyUseCaseTest {

    public static final String CNPJ = "1234567890112";
    public static final String NAME_COMPANY = "Minha empresa";
    public static final String COMPANY_NOT_FOUND_404 = "Empresa não encontrada";
    public static final String COMPANY_ALREADY_EXIST_400 = "Empresa/CNPJ já cadastrado no sistema";

    @Mock
    private CompanyRepository companyRepository;
    @InjectMocks
    private CompanyUseCase companyUseCase;
    private Company company;
    private CompanyRequestDto companyRequestDto;
    private UpdateCompanyRequestDto updateCompanyRequestDto;

    @BeforeEach
    void setUp() {
        companyUseCase = new CompanyUseCase(companyRepository);
        UUID id = UUID.randomUUID();
        company = Company.builder()
                .id(id)
                .cnpj(CNPJ)
                .build();

        companyRequestDto = new CompanyRequestDto(
                CNPJ,
                NAME_COMPANY
        );
        updateCompanyRequestDto = new UpdateCompanyRequestDto(
                CNPJ,
                NAME_COMPANY
        );
    }

    @Test
    void shouldReturnEmployeeById_Success() {
        when(companyRepository.findCompany(company.getId())).thenReturn(Optional.of(company));

        var response = companyUseCase.findCompanyById(company.getId());

        assertNotNull(response);
        assertEquals(company.getId(), response.id());
        assertEquals(company.getNameCompany(), response.nameCompany());
        assertEquals(company.getCnpj(), response.cnpj());
    }

    @Test
    void shouldReturnAllEmployees_Success() {
        UUID id2 = UUID.randomUUID();
        var company2 = Company.builder()
                .id(id2)
                .cnpj("1234567890012")
                .nameCompany("NAME")
                .build();
        List<Company> companies = Arrays.asList(company, company2);

        PaginatedList<Company> companyPaginated = new PaginatedList<>(
                companies, 0, 2, 2L
        );

        when(companyRepository.findAllCompanies(0, 2)).thenReturn(companyPaginated);
        PaginatedList<CompanyResponseDto> response = companyUseCase.findAllCompanies(0, 2);

        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals(0, response.getPageNumber());
        assertEquals(2, response.getPageSize());
        assertEquals(2L, response.getTotalElements());
        verify(companyRepository, times(1)).findAllCompanies(0, 2);
    }

    @Test
    void shouldCreateEmployee_Success() {
        when(companyRepository.findCnpj(companyRequestDto.cnpj())).thenReturn(Optional.empty());
        companyUseCase.addCompany(companyRequestDto);

        ArgumentCaptor<Company> companyCaptor = ArgumentCaptor.forClass(Company.class);
        verify(companyRepository, times(1)).saveCompany(companyCaptor.capture());
        var savedCompany = companyCaptor.getValue();

        assertEquals(companyRequestDto.nameCompany(), savedCompany.getNameCompany());
        assertEquals(companyRequestDto.cnpj(), savedCompany.getCnpj());
        verify(companyRepository, times(1)).findCnpj(companyRequestDto.cnpj());
    }

    @Test
    void shouldUpdateEmployee_Success() {
        when(companyRepository.findCompany(company.getId())).thenReturn(Optional.of(company));
        companyUseCase.updateCompany(company.getId(), updateCompanyRequestDto);

        ArgumentCaptor<Company> companyCaptor = ArgumentCaptor.forClass(Company.class);
        verify(companyRepository, times(1)).saveCompany(companyCaptor.capture());
        var savedCompany = companyCaptor.getValue();

        verify(companyRepository, times(1)).findCompany(company.getId());
        assertDoesNotThrow(() -> companyUseCase.updateCompany(company.getId(), updateCompanyRequestDto));
        assertEquals(updateCompanyRequestDto.nameCompany(), savedCompany.getNameCompany());
        assertEquals(updateCompanyRequestDto.cnpj(), savedCompany.getCnpj());
    }

    @Test
    void shouldDeleteEmployee_Success() {
        when(companyRepository.findCompany(company.getId())).thenReturn(Optional.of(company));

        assertDoesNotThrow(() -> companyUseCase.deleteCompany(company.getId()));

        verify(companyRepository, times(1)).findCompany(company.getId());
        verify(companyRepository, times(1)).deleteCompany(company.getId());
        verifyNoMoreInteractions(companyRepository);
    }

    @Test
    void shouldNotReturnEmployeeById_NotFoundExceptio() {
        when(companyRepository.findCompany(company.getId())).thenReturn(Optional.empty());
        var notFoundException = assertThrows(NotFoundException.class,
                () -> companyUseCase.findCompanyById(company.getId()));

        assertEquals(COMPANY_NOT_FOUND_404, notFoundException.getMessage());
        verify(companyRepository, times(1)).findCompany(company.getId());
    }

    @Test
    void shouldReturnEmployeeAlreadyExists_BadRequestException() {
        when(companyRepository.findCnpj(company.getCnpj())).thenReturn(Optional.of(company));
        var badRequestException = assertThrows(BadRequestException.class,
                () -> companyUseCase.addCompany(companyRequestDto));
        assertEquals(COMPANY_ALREADY_EXIST_400, badRequestException.getMessage());
        verify(companyRepository, never()).saveCompany(company);
    }

    @Test
    void shouldNotUpdateEmployee_NotFoundExceptio() {
        when(companyRepository.findCompany(company.getId())).thenReturn(Optional.empty());
        var notFoundException = assertThrows(NotFoundException.class,
                () -> companyUseCase.updateCompany(company.getId(), updateCompanyRequestDto));
        assertEquals(COMPANY_NOT_FOUND_404, notFoundException.getMessage());
        verify(companyRepository, times(1)).findCompany(company.getId());
    }

    @Test
    void shouldNotDeleteEmployee_NotFoundException() {
        when(companyRepository.findCompany(company.getId())).thenReturn(Optional.empty());

        var notFoundException = assertThrows(NotFoundException.class,
                () -> companyUseCase.deleteCompany(company.getId()));

        assertEquals(COMPANY_NOT_FOUND_404, notFoundException.getMessage());
        verify(companyRepository, times(1)).findCompany(company.getId());
    }
}