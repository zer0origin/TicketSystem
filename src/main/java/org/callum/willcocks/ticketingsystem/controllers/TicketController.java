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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class TicketController {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ParticipantRepository participantRepository;

    public TicketController(TicketRepository ticketRepository, UserRepository userRepository, MessageRepository messageRepository, ParticipantRepository participantRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.participantRepository = participantRepository;
    }

    @GetMapping("/")
    public String listTickets(Model model, Authentication authentication) {
        if (authentication.getAuthorities().stream().noneMatch(a -> Objects.equals(a.getAuthority(), "ROLE_view-all-tickets"))) {
            User user = userRepository.findUserByDisplayName(authentication.getName()).orElseGet(() -> {
                User newUser = new User(authentication.getName());
                userRepository.save(newUser);
                return newUser;
            });

            List<TicketParticipants> byParticipant = participantRepository.findByParticipant(user);

            List<Ticket> ticketsToShowUser = new ArrayList<>();
            ticketsToShowUser.addAll(byParticipant.stream().map(TicketParticipants::getTicket).toList());
            ticketsToShowUser.addAll(ticketRepository.findTicketByCreatedBy(user));

            model.addAttribute("tickets", ticketsToShowUser);
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
    public String deleteTicket(@PathVariable("id") UUID id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid item Id:" + id));

        ticketRepository.delete(ticket);
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") UUID id, Model model) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid item Id:" + id));

        model.addAttribute("ticket", ticket);
        return "update-ticket";
    }

    @PostMapping("/update/{id}")
    public String updateTicket(@PathVariable("id") UUID id, Ticket ticket) {
        ticket.setId(id);
        ticket.setName(ticket.getName().toUpperCase());
        ticketRepository.save(ticket);
        return "redirect:/";
    }

    @GetMapping("/view/{ticket_id}")
    public String showViewForm(@PathVariable("ticket_id") UUID ticketId, @RequestParam Optional<String> findUserErrorMessage, Model model, Authentication authentication) {
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);

        if (ticket.isEmpty()){
            System.out.println("Ticket was not provided!");
            return "redirect:/";
        }

        User user = userRepository.findUserByDisplayName(authentication.getName()).orElseGet(() -> {
            User newUser = new User(authentication.getName());
            userRepository.save(newUser);
            return newUser;
        });

        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority -> Objects.equals(grantedAuthority.getAuthority(), "ROLE_view-all-tickets"))){
            List<TicketParticipants> byParticipant = participantRepository.findByParticipant(user);
            boolean isUserParticipatingInTicket = byParticipant.stream().map(TicketParticipants::getTicket).anyMatch(t -> t.getId().equals(ticket.get().getId()));

            if (!Objects.equals(authentication.getName(), ticket.get().getCreatedBy().getDisplayName()) && !isUserParticipatingInTicket){
                System.out.println("User does not have permission to view this ticket!");
                return "redirect:/";
            }
        }

        model.addAttribute("ticket", ticket.get());
        model.addAttribute("user", user);
        model.addAttribute("message", new Message());

        model.addAttribute("usernameToSearch", "");
        model.addAttribute("findUserErrorMessage", findUserErrorMessage);
        model.addAttribute("usersInTicket", ticket.get().getTicketParticipants());

        List<Message> messages = messageRepository.findByTicket(ticket.get());
        model.addAttribute("messages", messages);

        return "view-ticket";
    }
}
