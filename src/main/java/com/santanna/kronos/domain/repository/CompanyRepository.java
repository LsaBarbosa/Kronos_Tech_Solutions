package com.santanna.kronos.domain.repository;

import com.santanna.kronos.domain.common.PaginatedList;
import com.santanna.kronos.domain.model.Company;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository {
    Optional<Company> findCompany(UUID companyId);
    Optional<Company> findCnpj(String cnpj);
    void saveCompany(Company company);
    PaginatedList<Company> findAllCompanies(int page, int size);
    void deleteCompany(UUID companyId);
}
