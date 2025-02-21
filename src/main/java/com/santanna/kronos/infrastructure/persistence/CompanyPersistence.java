package com.santanna.kronos.infrastructure.persistence;

import com.santanna.kronos.domain.common.PaginatedList;
import com.santanna.kronos.infrastructure.entity.CompanyEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyPersistence extends JpaRepository<CompanyEntity, UUID> {

    @Query("SELECT company FROM CompanyEntity company WHERE company.cnpj = :cnpj ")
    Optional<CompanyEntity> findCompanyByCnpj(@Param("cnpj") String cnpj);

    @Query("SELECT c FROM CompanyEntity c LEFT JOIN FETCH c.employees WHERE c.id = :companyId")
    Optional<CompanyEntity> findByIdWithEmployees(@Param("companyId") UUID companyId);

}
