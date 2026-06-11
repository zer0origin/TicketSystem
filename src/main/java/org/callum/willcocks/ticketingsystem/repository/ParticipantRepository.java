package org.callum.willcocks.ticketingsystem.repository;

import org.callum.willcocks.ticketingsystem.models.TicketParticipants;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ParticipantRepository extends JpaRepository<TicketParticipants, UUID> {
}
