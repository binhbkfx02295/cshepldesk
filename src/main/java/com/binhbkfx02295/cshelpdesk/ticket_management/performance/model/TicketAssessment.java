package com.binhbkfx02295.cshelpdesk.ticket_management.performance.model;

import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.entity.Satisfaction;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
@Data
@Entity
public class TicketAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private long ticketId;

    @Column(nullable = false)
    private Timestamp createdAt;

    @Column(nullable = false, length = 30)
    private String evaluatedAssignee;

    @Column(nullable = false)
    private boolean evaluated;

    @Column(nullable = false)
    private boolean passed;

    @Column(nullable = false)
    private Timestamp evaluatedAt;

    @Column(nullable = false)
    private String evaluatedBy;

    @Column(length = 256)
    private String summary;

    @Column(nullable = false)
    private float firstResponseTime;

    @Column(nullable = false)
    private float avgResponseTime;

    @Column(nullable = false)
    private float resolutionTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "satisfaction_id")
    private Satisfaction satisfaction;

    @ManyToMany()
    @JoinTable(name = "ticket_assessment_criteria",
    joinColumns = @JoinColumn(name = "ticket_assessment_id"),
    inverseJoinColumns = @JoinColumn(name = "criteria_id"))
    private List<Criteria> failedCriterias;
}
