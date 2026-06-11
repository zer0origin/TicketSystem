package org.callum.willcocks.ticketingsystem.models;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@IdClass(TicketParticipants.ParticipantId.class)
public class TicketParticipants {
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User participant;

    @Id
    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    public User getParticipant() {
        return participant;
    }

    public void setParticipant(User participant) {
        this.participant = participant;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public static class ParticipantId implements Serializable {
        private User participant;
        private Ticket ticket;

        public ParticipantId() {
        }

        public ParticipantId(User participant, Ticket ticket) {
            this.participant = participant;
            this.ticket = ticket;
        }
    }
}
