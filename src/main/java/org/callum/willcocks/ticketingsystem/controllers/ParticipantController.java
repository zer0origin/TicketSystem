package org.callum.willcocks.ticketingsystem.controllers;

import org.callum.willcocks.ticketingsystem.models.Ticket;
import org.callum.willcocks.ticketingsystem.models.TicketParticipants;
import org.callum.willcocks.ticketingsystem.models.User;
import org.callum.willcocks.ticketingsystem.repository.ParticipantRepository;
import org.callum.willcocks.ticketingsystem.repository.TicketRepository;
import org.callum.willcocks.ticketingsystem.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Controller
public class ParticipantController {
    private final ParticipantRepository participantRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public ParticipantController(ParticipantRepository participantRepository, TicketRepository ticketRepository, UserRepository userRepository) {
        this.participantRepository = participantRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/{ticket_id}/participant/add")
    public String addParticipant(@PathVariable("ticket_id") UUID ticketId, String usernameToSearch, Authentication authentication){
        TicketParticipants ticketParticipants = new TicketParticipants();

        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new IllegalArgumentException("UUID does not exist!"));
        ticketParticipants.setTicket(ticket);

        if (Objects.equals(authentication.getName(), usernameToSearch) || Objects.equals(ticket.getCreatedBy().getDisplayName(), usernameToSearch)){
            return "redirect:/view/" + ticketId + "?findUserErrorMessage=UserAlreadyHere!";
        }

        Optional<User> user = userRepository.findUserByDisplayName(usernameToSearch);
        if (user.isEmpty()){
            return "redirect:/view/" + ticketId + "?findUserErrorMessage=UserNotFound";
        }

        ticketParticipants.setParticipant(user.get());
        participantRepository.save(ticketParticipants);
        return "redirect:/view/" + ticketId;
    }

    @PostMapping("/{ticket_id}/participant/remove/{display_name}")
    public String removeParticipant(@PathVariable("ticket_id") UUID ticketId, @PathVariable("display_name") String displayName, Authentication authentication){
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new IllegalArgumentException("UUID does not exist!"));

        if (!authentication.getName().equals(ticket.getCreatedBy().getDisplayName()) || authentication.getAuthorities().stream().noneMatch(a -> Objects.equals(a.getAuthority(), "ROLE_view-all-tickets"))){
            System.out.println("Insufficient Permissions.");
            return "redirect:/view/" + ticketId;
        }

        Optional<User> user = userRepository.findUserByDisplayName(displayName);
        if (user.isEmpty()){
            System.out.println("No user provided.");
            return "redirect:/view/" + ticketId;
        }

        TicketParticipants byTicketAndParticipant = participantRepository.findOneByTicketAndParticipant(ticket, user.get());
        participantRepository.delete(byTicketAndParticipant);

        return "redirect:/view/" + ticketId;
    }
}
