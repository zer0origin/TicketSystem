package org.callum.willcocks.ticketingsystem;

import org.callum.willcocks.ticketingsystem.models.Message;
import org.callum.willcocks.ticketingsystem.models.Ticket;
import org.callum.willcocks.ticketingsystem.repository.MessageRepository;
import org.callum.willcocks.ticketingsystem.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @PostMapping("/msg/add/{id}")
    public String AddMessageToTicket(@PathVariable long id, Message message){
        Optional<Ticket> ticket = ticketRepository.findById(id);

        if (ticket.isEmpty()){
            return "redirect:/";
        }

        message.setTicket(ticket.get());
        System.out.printf("Adding message to ticket: %d\n", ticket.get().getId());
        messageRepository.save(message);
        System.out.println("Message Saved!\n");

        return "redirect:/";
    }
}
