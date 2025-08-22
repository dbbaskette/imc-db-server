package com.insurancemegacorp.dbserver.repository;

import com.insurancemegacorp.dbserver.model.DriverAccidentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverAccidentModelRepository extends JpaRepository<DriverAccidentModel, Integer> {

    // Since this is a MADlib output table with typically one row, just get the first one
    @Query("SELECT m FROM DriverAccidentModel m")
    Optional<DriverAccidentModel> findLatestActiveModel();

    @Query("SELECT m FROM DriverAccidentModel m")
    Optional<DriverAccidentModel> findLatestModel();
}