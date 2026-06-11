package org.callum.willcocks.ticketingsystem.models;
import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String description;
    private Byte priority;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User createdBy;

    @OneToMany
    @JoinColumn(name = "ticket_id")
    private List<TicketParticipants> ticketParticipants;

    public Ticket() {
    }

    public Ticket(User createdBy) {
        this.createdBy = createdBy;
    }

    public Ticket(String name, String description, Byte priority) {
        this.name = name;
        this.description = description;
        this.priority = priority;
    }

    public Ticket(String name, String description, Byte priority, User createdBy) {
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.createdBy = createdBy;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Byte getPriority() {
        return priority;
    }

    public void setPriority(Byte priority) {
        this.priority = priority;
    }

}
