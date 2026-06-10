package org.callum.willcocks.ticketingsystem.repository;

import org.callum.willcocks.ticketingsystem.models.Ticket;
import org.callum.willcocks.ticketingsystem.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    List<Ticket> findTicketByCreatedBy(User createdBy);
}
