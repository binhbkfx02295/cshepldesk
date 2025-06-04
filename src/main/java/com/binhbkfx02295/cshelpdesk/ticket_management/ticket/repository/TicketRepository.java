package com.binhbkfx02295.cshelpdesk.ticket_management.ticket.repository;

import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketReportDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketVolumeReportDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
            "LEFT JOIN FETCH t.assignee u " +
            "LEFT JOIN FETCH u.userGroup " +
            "LEFT JOIN FETCH t.notes " +
            "WHERE t.id = :id ")
    @EntityGraph(attributePaths = {
            "facebookUser", "emotion", "satisfaction",
            "progressStatus", "facebookUser", "category",
            "firstResponseRate", "overallResponseRate", "resolutionRate"
    })
    Optional<Ticket> findByIdWithDetails(@Param("id") int id);

    @Query("""
    SELECT t FROM Ticket t 
    LEFT JOIN FETCH t.assignee u
    LEFT JOIN FETCH u.userGroup 
    LEFT JOIN FETCH t.messages m 
    WHERE 
        (m.timestamp = ( 
            SELECT MAX(m2.timestamp) 
            FROM Message m2 
            WHERE m2.ticket = t 
        ) OR m IS NULL) 
        AND (t.createdAt BETWEEN :startOfDay AND :endOfDay 
        OR t.progressStatus.id <> 3) 
""")
    @EntityGraph(attributePaths = {
            "facebookUser", "progressStatus", "satisfaction",
            "progressStatus", "facebookUser", "category"
    })
    List<Ticket> findOpeningOrToday(@Param("startOfDay") Timestamp startOfDay,
                                    @Param("endOfDay") Timestamp endOfDay);

    @EntityGraph(attributePaths = {
            "assignee", "facebookUser", "category", "progressStatus", "emotion", "satisfaction"
    })
    @Override
    Page<Ticket> findAll(Specification<Ticket> spec, Pageable pageable);

    @Query("""
    SELECT new com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketReportDTO(
        t.id, t.firstResponseRate, t.overallResponseRate, 
        t.resolutionRate, t.createdAt)
    FROM Ticket t
    WHERE t.progressStatus.id = 3
      AND t.createdAt BETWEEN :startTime AND :endTime
      AND t.firstResponseRate IS NOT NULL
      AND t.overallResponseRate IS NOT NULL
      AND t.resolutionRate IS NOT NULL
""")
    List<TicketReportDTO> findTicketsWithMetrics(@Param("startTime")Timestamp startTime,
                                                 @Param("endTime")Timestamp endTime);

    @Query("""
            SELECT new com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketVolumeReportDTO 
            (t.id, t.createdAt) 
            FROM Ticket t 
            WHERE t.createdAt BETWEEN :fromTime AND :toTime
            """)
    List<TicketVolumeReportDTO> findTicketsForHourlyReport(@Param("fromTime") Timestamp fromTime,
                                                           @Param("toTime") Timestamp toTime);
}