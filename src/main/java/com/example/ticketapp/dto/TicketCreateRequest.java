package com.example.ticketapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TicketCreateRequest {

    @NotNull(message = "Event date is required")
    private LocalDateTime date;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @NotBlank(message = "Owner name is required")
    private String ownerName;

    @NotBlank(message = "Show title is required")
    private String showTitle;

    @NotBlank(message = "Venue is required")
    private String venue;

    public TicketCreateRequest() {}

    public TicketCreateRequest(LocalDateTime date, BigDecimal price, String ownerName, String showTitle, String venue) {
        this.date = date;
        this.price = price;
        this.ownerName = ownerName;
        this.showTitle = showTitle;
        this.venue = venue;
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
}