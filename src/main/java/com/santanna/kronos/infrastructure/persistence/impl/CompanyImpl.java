package com.santanna.kronos.infrastructure.persistence.impl;

import com.santanna.kronos.domain.common.PaginatedList;
import com.santanna.kronos.domain.model.Company;
import com.santanna.kronos.domain.repository.CompanyRepository;
import com.santanna.kronos.infrastructure.exception.DatabaseException;
import com.santanna.kronos.infrastructure.mapper.ConverterDomainEntity;
import com.santanna.kronos.infrastructure.persistence.CompanyPersistence;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CompanyImpl implements CompanyRepository {
    public static final String COMPANY_ID_NOT_FOUND = "Error company ID not found: ";
    public static final String COMPANY_CNPJ_NOT_FOUND = "Error company CNPJ not found: ";
    public static final String ERROR_SAVING_COMPANY = "Error saving Company";
    public static final String ERROR_DELETING_EMPLOYEE = "Error deleting employee";
    private final CompanyPersistence companyPersistence;

    public CompanyImpl(CompanyPersistence companyPersistence) {
        this.companyPersistence = companyPersistence;
    }

    @Override
    public Optional<Company> findCompany(UUID companyId) {
        try {
            return companyPersistence.findByIdWithEmployees(companyId).map(ConverterDomainEntity::toDomain);
        } catch (DataAccessException ex) {
            throw new DatabaseException(COMPANY_ID_NOT_FOUND + companyId, ex);
        }
    }

    @Override
    public Optional<Company> findCnpj(String cnpj) {
        try {
            return companyPersistence.findByCnpj(cnpj).map(ConverterDomainEntity::toDomain);
        } catch (DataAccessException ex) {
            throw new DatabaseException(COMPANY_CNPJ_NOT_FOUND + cnpj, ex);
        }
    }

    @Override
    public PaginatedList<Company> findAllCompanies(int page, int size) {
        try {
            var entityPage = companyPersistence.findAll(PageRequest.of(page, size));
            return new PaginatedList<>(
                    entityPage.getContent().stream().map(ConverterDomainEntity::toDomain)
                            .collect(Collectors.toList()),
                    entityPage.getNumber(),
                    entityPage.getSize(),
                    entityPage.getTotalElements()
            );
        } catch (DataAccessException ex) {
            throw new DatabaseException(COMPANY_CNPJ_NOT_FOUND, ex);
        }
    }

    @Override
    public void saveCompany(Company company) {
        try {
            var companyEntity = ConverterDomainEntity.toEntity(company);
            var savedEntity = this.companyPersistence.save(companyEntity);
            ConverterDomainEntity.toDomain(savedEntity);
        } catch (DataAccessException ex) {
            throw new DatabaseException(ERROR_SAVING_COMPANY, ex);
        }
    }

    @Override
    public void deleteCompany(UUID companyId) {
        try {
            companyPersistence.deleteById(companyId);
        } catch (DataAccessException ex) {
            throw new DatabaseException(ERROR_DELETING_EMPLOYEE, ex);
        }
    }
}
