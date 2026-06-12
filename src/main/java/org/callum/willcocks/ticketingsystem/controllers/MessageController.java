package org.callum.willcocks.ticketingsystem.controllers;

import org.callum.willcocks.ticketingsystem.models.Message;
import org.callum.willcocks.ticketingsystem.models.Ticket;
import org.callum.willcocks.ticketingsystem.models.TicketParticipants;
import org.callum.willcocks.ticketingsystem.models.User;
import org.callum.willcocks.ticketingsystem.repository.MessageRepository;
import org.callum.willcocks.ticketingsystem.repository.ParticipantRepository;
import org.callum.willcocks.ticketingsystem.repository.TicketRepository;
import org.callum.willcocks.ticketingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Controller
public class MessageController {
    private final MessageRepository messageRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public MessageController(MessageRepository messageRepository, TicketRepository ticketRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/msg/add/{ticket_id}")
    public String AddMessageToTicket(@PathVariable("ticket_id") UUID ticketId, Principal principal, Message message){
        Ticket ticket = ticketRepository.getReferenceById(ticketId);
        Optional<User> user = userRepository.findUserByDisplayName(principal.getName()).or(() -> {
            User newUser = new User(principal.getName());
            userRepository.save(newUser);
            return Optional.of(newUser);
        });

        if (user.isEmpty()){
            return "redirect:/";
        }

        message.setTicket(ticket);
        message.setUser(user.get());
        messageRepository.save(message);

        return "redirect:/view/" + ticketId;
    }
}
