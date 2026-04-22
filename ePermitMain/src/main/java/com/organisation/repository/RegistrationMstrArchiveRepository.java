package com.organisation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.register.model.RegistrationMstrArchive;

@Repository
public interface RegistrationMstrArchiveRepository 
        extends JpaRepository<RegistrationMstrArchive, Long> {

    List<RegistrationMstrArchive> findByOrgId(String orgId);
    
    // Find latest archive by Org ID (based on archiveCreatedOn)
    RegistrationMstrArchive findTopByOrgIdOrderByArchiveCreatedOnDesc(String orgId);


}
