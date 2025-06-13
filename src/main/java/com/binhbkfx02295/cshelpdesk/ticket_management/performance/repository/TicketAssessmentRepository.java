package com.binhbkfx02295.cshelpdesk.ticket_management.performance.repository;

import com.binhbkfx02295.cshelpdesk.ticket_management.performance.model.TicketAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketAssessmentRepository extends JpaRepository<TicketAssessment, Long> {


    Optional<TicketAssessment> findByTicketId(Long id);

    List<TicketAssessment> findAllByEvaluatedTrue();
    @Query("""
            SELECT ta
            FROM Ticket t, TicketAssessment ta
            WHERE  t.id = ta.ticketId
            AND t.assignee.username = :username
            AND ta.evaluated = true
            AND t.createdAt BETWEEN :fromTime AND :toTime
            """)
    List<TicketAssessment> findEvaluatedByMonth(@Param("username") String username,
                                                @Param("fromTime") Timestamp fromTime,
                                                @Param(("toTime")) Timestamp toTime);
}
