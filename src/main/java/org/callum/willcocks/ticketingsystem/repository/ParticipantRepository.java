package org.callum.willcocks.ticketingsystem.repository;

import org.callum.willcocks.ticketingsystem.models.Ticket;
import org.callum.willcocks.ticketingsystem.models.TicketParticipants;
import org.callum.willcocks.ticketingsystem.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ParticipantRepository extends JpaRepository<TicketParticipants, UUID> {
    List<TicketParticipants> findByParticipant(User participant);
    TicketParticipants findOneByTicketAndParticipant(Ticket ticket, User participant);
}
