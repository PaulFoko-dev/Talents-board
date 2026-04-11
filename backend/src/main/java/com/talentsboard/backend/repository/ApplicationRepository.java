package com.talentsboard.backend.repository;

import com.talentsboard.backend.model.Application;
import com.talentsboard.backend.model.Application.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByCandidateId(Long candidateId);
    List<Application> findByTicketId(Long ticketId);
    List<Application> findByStatus(ApplicationStatus status);
    boolean existsByCandidateIdAndTicketId(Long candidateId, Long ticketId);
}
