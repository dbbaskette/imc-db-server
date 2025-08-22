package com.insurancemegacorp.dbserver.repository;

import com.insurancemegacorp.dbserver.model.DriverMlTrainingData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverMlTrainingDataRepository extends JpaRepository<DriverMlTrainingData, Long> {
}