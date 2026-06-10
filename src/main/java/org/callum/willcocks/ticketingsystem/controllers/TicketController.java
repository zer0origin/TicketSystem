package org.callum.willcocks.ticketingsystem.controllers;

import org.callum.willcocks.ticketingsystem.models.Message;
import org.callum.willcocks.ticketingsystem.models.Ticket;
import org.callum.willcocks.ticketingsystem.models.User;
import org.callum.willcocks.ticketingsystem.repository.MessageRepository;
import org.callum.willcocks.ticketingsystem.repository.TicketRepository;
import org.callum.willcocks.ticketingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    public TicketController() {
    }

    @GetMapping("/")
    public String listTickets(Model model, Authentication authentication) {
        if (authentication.getAuthorities().stream().noneMatch(a -> Objects.equals(a.getAuthority(), "ROLE_view-all-tickets"))) {
            Optional<User> user = userRepository.findUserByDisplayName(authentication.getName()).or(() -> {
                User newUser = new User(authentication.getName());
                userRepository.save(newUser);
                return Optional.of(newUser);
            });

            model.addAttribute("tickets", ticketRepository.findTicketByCreatedBy(user.get()));
            return "list-tickets";
        }

        model.addAttribute("tickets", ticketRepository.findAll());
        return "list-tickets";
    }

    @GetMapping("/add")
    public String showAddForm(Model model, Principal principal) {
        model.addAttribute("ticket", new Ticket());

        return "add-ticket";
    }

    @PostMapping("/add")
    public String addTicket(Ticket ticket, Principal principal) {
        Optional<User> user = userRepository.findUserByDisplayName(principal.getName()).or(() -> {
            User newUser = new User(principal.getName());
            userRepository.save(newUser);
            return Optional.of(newUser);
        });

        ticket.setName(ticket.getName().toUpperCase());
        ticket.setCreatedBy(user.get());
        ticketRepository.save(ticket);

        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteTicket(@PathVariable("id") long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid item Id:" + id));

        ticketRepository.delete(ticket);
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid item Id:" + id));

        model.addAttribute("ticket", ticket);
        return "update-ticket";
    }

    @PostMapping("/update/{id}")
    public String updateTicket(@PathVariable("id") long id, Ticket ticket) {
        ticket.setId(id);
        ticket.setName(ticket.getName().toUpperCase());
        ticketRepository.save(ticket);
        return "redirect:/";
    }
}
