package com.talentsboard.backend.repository;

import com.talentsboard.backend.model.Ticket;
import com.talentsboard.backend.model.Ticket.TicketStatus;
import com.talentsboard.backend.model.Ticket.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByOwnerId(Long ownerId);
    List<Ticket> findByType(TicketType type);
    List<Ticket> findByStatus(TicketStatus status);
    List<Ticket> findByTypeAndStatus(TicketType type, TicketStatus status);
}
