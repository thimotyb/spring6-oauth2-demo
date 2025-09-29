package com.example.ticketapp.repository;

import com.example.ticketapp.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByOwnerNameContainingIgnoreCase(String ownerName);

    List<Ticket> findByShowTitleContainingIgnoreCase(String showTitle);

    List<Ticket> findByVenueContainingIgnoreCase(String venue);

    @Query("SELECT t FROM Ticket t WHERE " +
           "(:ownerName IS NULL OR LOWER(t.ownerName) LIKE LOWER(CONCAT('%', :ownerName, '%'))) AND " +
           "(:showTitle IS NULL OR LOWER(t.showTitle) LIKE LOWER(CONCAT('%', :showTitle, '%'))) AND " +
           "(:venue IS NULL OR LOWER(t.venue) LIKE LOWER(CONCAT('%', :venue, '%')))")
    List<Ticket> findTicketsWithFilters(@Param("ownerName") String ownerName,
                                       @Param("showTitle") String showTitle,
                                       @Param("venue") String venue);
}