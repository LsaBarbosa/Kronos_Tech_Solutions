package com.santanna.kronos.infrastructure.persistence;

import com.santanna.kronos.domain.model.Employee;
import com.santanna.kronos.infrastructure.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeePersistence extends JpaRepository<EmployeeEntity, UUID> {

    @Query("SELECT employee FROM EmployeeEntity employee WHERE employee.cpf = :cpf ")
    Optional <EmployeeEntity> findEmployeeByCpf(@Param("cpf")String cpf);

}
