package org.callum.willcocks.ticketingsystem.controllers;

import org.callum.willcocks.ticketingsystem.models.Message;
import org.callum.willcocks.ticketingsystem.models.Ticket;
import org.callum.willcocks.ticketingsystem.repository.MessageRepository;
import org.callum.willcocks.ticketingsystem.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class MessageController {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private TicketRepository ticketRepository;

    public MessageController() {
    }

    @PostMapping("/msg/add/{ticket_id}")
    public String AddMessageToTicket(@PathVariable("ticket_id") long ticketId, Message message){
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);

        if (ticket.isEmpty()){
            return "redirect:/";
        }

        message.setTicket(ticket.get());
        messageRepository.save(message);
        System.out.println("Message Saved!\n");

        return "redirect:/view/" + ticketId;
    }
}
