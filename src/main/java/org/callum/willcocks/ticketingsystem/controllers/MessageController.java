package org.callum.willcocks.ticketingsystem.controllers;

import org.callum.willcocks.ticketingsystem.models.Message;
import org.callum.willcocks.ticketingsystem.models.Ticket;
import org.callum.willcocks.ticketingsystem.models.User;
import org.callum.willcocks.ticketingsystem.repository.MessageRepository;
import org.callum.willcocks.ticketingsystem.repository.TicketRepository;
import org.callum.willcocks.ticketingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class MessageController {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    public MessageController() {
    }

    @GetMapping("/view/{ticket_id}")
    public String showViewForm(@PathVariable("ticket_id") long ticketId, Model model, Principal principal) {
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        Optional<User> user = userRepository.findUserByDisplayName(principal.getName()).or(() -> {
            User newUser = new User(principal.getName());
            userRepository.save(newUser);
            return Optional.of(newUser);
        });

        if (ticket.isEmpty() || user.isEmpty()){
            System.out.println("Ticket or User was not provided!");
            return "redirect:/";
        }

        model.addAttribute("ticket", ticket.get());
        model.addAttribute("user", user.get());
        model.addAttribute("message", new Message());

        List<Message> messages = messageRepository.findByTicket(ticket.get());
        model.addAttribute("messages", messages);

        return "view-ticket";
    }

    @PostMapping("/msg/add/{ticket_id}")
    public String AddMessageToTicket(@PathVariable("ticket_id") long ticketId, Principal principal, Message message){
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
