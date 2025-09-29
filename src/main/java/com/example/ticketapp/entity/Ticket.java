package com.example.ticketapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "event_date", nullable = false)
    private LocalDateTime date;

    @NotNull
    @Positive
    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @NotBlank
    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @NotBlank
    @Column(name = "show_title", nullable = false)
    private String showTitle;

    @NotBlank
    @Column(name = "venue", nullable = false)
    private String venue;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Ticket() {}

    public Ticket(LocalDateTime date, BigDecimal price, String ownerName, String showTitle, String venue) {
        this.date = date;
        this.price = price;
        this.ownerName = ownerName;
        this.showTitle = showTitle;
        this.venue = venue;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
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

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", date=" + date +
                ", price=" + price +
                ", ownerName='" + ownerName + '\'' +
                ", showTitle='" + showTitle + '\'' +
                ", venue='" + venue + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}