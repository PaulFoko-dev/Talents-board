package com.talentsboard.backend.service;

import com.talentsboard.backend.dto.TicketDTO;
import com.talentsboard.backend.model.Ticket;
import com.talentsboard.backend.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public List<TicketDTO> getAllTickets() {
        return ticketRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public TicketDTO getTicketById(Long id) {
        return toDTO(ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ticket non trouvé: " + id)));
    }

    public List<TicketDTO> getTicketsByOwner(Long ownerId) {
        return ticketRepository.findByOwnerId(ownerId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<TicketDTO> getOpenTicketsByType(Ticket.TicketType type) {
        return ticketRepository.findByTypeAndStatus(type, Ticket.TicketStatus.OPEN)
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public TicketDTO createTicket(TicketDTO dto) {
        Ticket ticket = toEntity(dto);
        return toDTO(ticketRepository.save(ticket));
    }

    public TicketDTO updateTicket(Long id, TicketDTO dto) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ticket non trouvé: " + id));
        ticket.setTitle(dto.getTitle());
        ticket.setDescription(dto.getDescription());
        ticket.setSkills(dto.getSkills());
        ticket.setLocation(dto.getLocation());
        if (dto.getStatus() != null) ticket.setStatus(dto.getStatus());
        return toDTO(ticketRepository.save(ticket));
    }

    public TicketDTO closeTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ticket non trouvé: " + id));
        ticket.setStatus(Ticket.TicketStatus.CLOSED);
        return toDTO(ticketRepository.save(ticket));
    }

    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }

    private TicketDTO toDTO(Ticket ticket) {
        TicketDTO dto = new TicketDTO();
        dto.setId(ticket.getId());
        dto.setOwnerId(ticket.getOwnerId());
        dto.setOwnerName(ticket.getOwnerName());
        dto.setType(ticket.getType());
        dto.setTitle(ticket.getTitle());
        dto.setDescription(ticket.getDescription());
        dto.setSkills(ticket.getSkills());
        dto.setLocation(ticket.getLocation());
        dto.setStatus(ticket.getStatus());
        if (ticket.getCreatedAt() != null) dto.setCreatedAt(ticket.getCreatedAt().toString());
        return dto;
    }

    private Ticket toEntity(TicketDTO dto) {
        Ticket ticket = new Ticket();
        ticket.setOwnerId(dto.getOwnerId());
        ticket.setOwnerName(dto.getOwnerName());
        ticket.setType(dto.getType());
        ticket.setTitle(dto.getTitle());
        ticket.setDescription(dto.getDescription());
        ticket.setSkills(dto.getSkills());
        ticket.setLocation(dto.getLocation());
        ticket.setStatus(dto.getStatus() != null ? dto.getStatus() : Ticket.TicketStatus.OPEN);
        return ticket;
    }
}
