package com.insurancemegacorp.dbserver.repository;

import com.insurancemegacorp.dbserver.model.DriverAccidentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverAccidentModelRepository extends JpaRepository<DriverAccidentModel, String> {

    @Query("SELECT m FROM DriverAccidentModel m WHERE m.status = 'active' ORDER BY m.createdDate DESC LIMIT 1")
    Optional<DriverAccidentModel> findLatestActiveModel();

    @Query("SELECT m FROM DriverAccidentModel m ORDER BY m.createdDate DESC LIMIT 1")
    Optional<DriverAccidentModel> findLatestModel();
}