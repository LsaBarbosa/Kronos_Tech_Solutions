package com.santanna.kronos.infrastructure.persistence.impl;

import com.santanna.kronos.domain.common.PaginatedList;
import com.santanna.kronos.domain.model.Company;
import com.santanna.kronos.domain.repository.CompanyRepository;
import com.santanna.kronos.infrastructure.entity.CompanyEntity;
import com.santanna.kronos.infrastructure.exception.DatabaseException;
import com.santanna.kronos.infrastructure.persistence.CompanyPersistence;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CompanyImpl implements CompanyRepository {
    private final CompanyPersistence companyPersistence;

    public CompanyImpl(CompanyPersistence companyPersistence) {
        this.companyPersistence = companyPersistence;
    }

    @Override
    public Optional<Company> findCompany(UUID companyId) {
        try {
            return companyPersistence.findById(companyId).map(this::toDomain);
        } catch (DataAccessException ex) {
            throw new DatabaseException("Error company ID not found: " + companyId, ex);
        }
    }

    @Override
    public Optional<Company> findCnpj(String cnpj) {
        try {
            return companyPersistence.findCompanyByCnpj(cnpj).map(this::toDomain);
        } catch (DataAccessException ex) {
            throw new DatabaseException("Error company CNPJ not found: " + cnpj, ex);
        }
    }

    @Override
    public PaginatedList<Company> findAllCompanies(int page, int size) {
        try {
            var entityPage = companyPersistence.findAll(PageRequest.of(page, size));
            return new PaginatedList<>(
                    entityPage.getContent().stream().map(this::toDomain)
                            .collect(Collectors.toList()),
                    entityPage.getNumber(),
                    entityPage.getSize(),
                    entityPage.getTotalElements()
            );
        } catch (DataAccessException ex) {
            throw new DatabaseException("Error! Companies not found", ex);
        }
    }

    @Override
    public void saveCompany(Company company) {
        try {
            var companyEntity = this.toEntity(company);
            var savedEntity = this.companyPersistence.save(companyEntity);
            toDomain(savedEntity);
        } catch (DataAccessException ex) {
            throw new DatabaseException("Error saving employee", ex);
        }
    }

    @Override
    public void deleteCompany(UUID companyId) {
        try {
            companyPersistence.deleteById(companyId);
        } catch (DataAccessException ex) {
            throw new DatabaseException("Error deleting employee", ex);
        }
    }

    private Company toDomain(CompanyEntity entity) {
        return new Company(
                entity.getId(),
                entity.getNameCompany(),
                entity.getCnpj()
        );
    }

    private CompanyEntity toEntity(Company domain) {
        return new CompanyEntity(
                domain.getId(),
                domain.getNameCompany(),
                domain.getCnpj()
        );
    }
}
