package com.binhbkfx02295.cshelpdesk.ticket_management.ticket.repository;

import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDetailDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer>, JpaSpecificationExecutor<Ticket> {
    Optional<Ticket> findFirstByFacebookUser_FacebookIdOrderByCreatedAtDesc(String facebookId);

    @EntityGraph(attributePaths = {
            "assignee", "facebookUser", "emotion", "satisfaction",
            "progressStatus", "facebookUser", "category"
    })
    List<Ticket> findAllByFacebookUser_FacebookId(String facebookId);

    @Query("SELECT DISTINCT t " +
            "FROM Ticket t " +
            "LEFT JOIN FETCH t.notes " +
            "WHERE t.id = :id")
    @EntityGraph(attributePaths = {
            "assignee", "facebookUser", "emotion", "satisfaction",
            "progressStatus", "facebookUser", "category"
    })
    Optional<Ticket> findByIdWithDetails(@Param("id") int id);

    @Query("""
    SELECT t FROM Ticket t
    WHERE t.progressStatus.id <> 3
       OR t.createdAt BETWEEN :startOfDay AND :endOfDay
""")
    List<Ticket> findOpeningOrToday(@Param("startOfDay") Timestamp startOfDay,
                                    @Param("endOfDay") Timestamp endOfDay);
}