package com.santanna.kronos.infrastructure.persistence;

import com.santanna.kronos.infrastructure.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeePersistence extends JpaRepository<EmployeeEntity, UUID> {
    Optional<EmployeeEntity> findByCpf(String cpf);

}
