package com.example.ticketapp.controller;

import com.example.ticketapp.dto.TicketCreateRequest;
import com.example.ticketapp.dto.TicketResponse;
import com.example.ticketapp.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<TicketResponse>> getAllTickets(
            @RequestParam(required = false) String ownerName,
            @RequestParam(required = false) String showTitle,
            @RequestParam(required = false) String venue) {

        List<TicketResponse> tickets;
        if (ownerName != null || showTitle != null || venue != null) {
            tickets = ticketService.searchTickets(ownerName, showTitle, venue);
        } else {
            tickets = ticketService.getAllTickets();
        }

        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id)
                .map(ticket -> ResponseEntity.ok(ticket))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<TicketResponse> createTicket(
            @Valid @RequestBody TicketCreateRequest request,
            Authentication authentication) {

        TicketResponse createdTicket = ticketService.createTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTicket);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<TicketResponse> updateTicket(
            @PathVariable Long id,
            @Valid @RequestBody TicketCreateRequest request,
            Authentication authentication) {

        return ticketService.updateTicket(id, request)
                .map(ticket -> ResponseEntity.ok(ticket))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteTicket(
            @PathVariable Long id,
            Authentication authentication) {

        boolean deleted = ticketService.deleteTicket(id);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "Ticket deleted successfully"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(Map.of(
                "username", authentication.getName(),
                "authorities", authentication.getAuthorities()
        ));
    }
}