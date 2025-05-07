package com.binhbkfx02295.cshelpdesk.ticket_management.ticket.repository;

import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer>, JpaSpecificationExecutor<Ticket> {
    Optional<Ticket> findFirstByFacebookUser_FacebookIdOrderByCreatedAtDesc(String facebookId);


    @Query("SELECT new com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDTO(" +
            "t.id, t.title, t.createdAt, e.username, t.progressStatus.code) " +
            "FROM Ticket t JOIN t.assignee e WHERE t.facebookUser.facebookId = :facebookId")
    List<TicketDTO> findAllByFacebookUser_FacebookId(String facebookId);

    @Query("SELECT t FROM Ticket t WHERE t.id = :id")
    @EntityGraph(attributePaths = {
            "assignee", "facebookUser", "emotion", "satisfaction",
            "progressStatus", "facebookUser"
    })
    Optional<Ticket> findByIdWithDetails(int id);

}