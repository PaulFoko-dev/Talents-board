package com.talentsboard.backend.service;

import com.talentsboard.backend.dto.ApplicationDTO;
import com.talentsboard.backend.model.Application;
import com.talentsboard.backend.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    public List<ApplicationDTO> getAllApplications() {
        return applicationRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ApplicationDTO getApplicationById(Long id) {
        return toDTO(applicationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Candidature non trouvée: " + id)));
    }

    public List<ApplicationDTO> getApplicationsByCandidate(Long candidateId) {
        return applicationRepository.findByCandidateId(candidateId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<ApplicationDTO> getApplicationsByTicket(Long ticketId) {
        return applicationRepository.findByTicketId(ticketId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ApplicationDTO createApplication(ApplicationDTO dto) {
        if (applicationRepository.existsByCandidateIdAndTicketId(dto.getCandidateId(), dto.getTicketId())) {
            throw new RuntimeException("Vous avez déjà postulé à ce ticket");
        }
        Application application = toEntity(dto);
        return toDTO(applicationRepository.save(application));
    }

    public ApplicationDTO updateStatus(Long id, Application.ApplicationStatus status) {
        Application application = applicationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Candidature non trouvée: " + id));
        application.setStatus(status);
        return toDTO(applicationRepository.save(application));
    }

    public void deleteApplication(Long id) {
        applicationRepository.deleteById(id);
    }

    private ApplicationDTO toDTO(Application app) {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setId(app.getId());
        dto.setCandidateId(app.getCandidateId());
        dto.setTicketId(app.getTicketId());
        dto.setMessage(app.getMessage());
        dto.setCvUrl(app.getCvUrl());
        dto.setStatus(app.getStatus());
        if (app.getCreatedAt() != null) dto.setCreatedAt(app.getCreatedAt().toString());
        return dto;
    }

    private Application toEntity(ApplicationDTO dto) {
        Application app = new Application();
        app.setCandidateId(dto.getCandidateId());
        app.setTicketId(dto.getTicketId());
        app.setMessage(dto.getMessage());
        app.setCvUrl(dto.getCvUrl());
        app.setStatus(Application.ApplicationStatus.PENDING);
        return app;
    }
}
