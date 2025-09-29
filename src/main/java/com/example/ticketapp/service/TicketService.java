package com.example.ticketapp.service;

import com.example.ticketapp.dto.TicketCreateRequest;
import com.example.ticketapp.dto.TicketResponse;
import com.example.ticketapp.entity.Ticket;
import com.example.ticketapp.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getAllTickets() {
        return ticketRepository.findAll()
                .stream()
                .map(TicketResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<TicketResponse> getTicketById(Long id) {
        return ticketRepository.findById(id)
                .map(TicketResponse::new);
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> searchTickets(String ownerName, String showTitle, String venue) {
        return ticketRepository.findTicketsWithFilters(ownerName, showTitle, venue)
                .stream()
                .map(TicketResponse::new)
                .toList();
    }

    public TicketResponse createTicket(TicketCreateRequest request) {
        Ticket ticket = new Ticket(
                request.getDate(),
                request.getPrice(),
                request.getOwnerName(),
                request.getShowTitle(),
                request.getVenue()
        );

        Ticket savedTicket = ticketRepository.save(ticket);
        return new TicketResponse(savedTicket);
    }

    public Optional<TicketResponse> updateTicket(Long id, TicketCreateRequest request) {
        return ticketRepository.findById(id)
                .map(ticket -> {
                    ticket.setDate(request.getDate());
                    ticket.setPrice(request.getPrice());
                    ticket.setOwnerName(request.getOwnerName());
                    ticket.setShowTitle(request.getShowTitle());
                    ticket.setVenue(request.getVenue());
                    return new TicketResponse(ticketRepository.save(ticket));
                });
    }

    public boolean deleteTicket(Long id) {
        if (ticketRepository.existsById(id)) {
            ticketRepository.deleteById(id);
            return true;
        }
        return false;
    }
}