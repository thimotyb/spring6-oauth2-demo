package com.example.ticketapp.dto;

import com.example.ticketapp.entity.Ticket;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TicketResponse {

    private Long id;
    private LocalDateTime date;
    private BigDecimal price;
    private String ownerName;
    private String showTitle;
    private String venue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TicketResponse() {}

    public TicketResponse(Ticket ticket) {
        this.id = ticket.getId();
        this.date = ticket.getDate();
        this.price = ticket.getPrice();
        this.ownerName = ticket.getOwnerName();
        this.showTitle = ticket.getShowTitle();
        this.venue = ticket.getVenue();
        this.createdAt = ticket.getCreatedAt();
        this.updatedAt = ticket.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getShowTitle() {
        return showTitle;
    }

    public void setShowTitle(String showTitle) {
        this.showTitle = showTitle;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}