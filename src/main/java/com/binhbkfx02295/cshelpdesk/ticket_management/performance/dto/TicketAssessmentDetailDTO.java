package com.binhbkfx02295.cshelpdesk.ticket_management.performance.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketAssessmentDetailDTO {
    private Long id;
    private Long ticketId;
    private String assignee;    // display-name
    private String evaluatedBy;
    private long evaluatedAt; // epoch millis (UTC)
    private boolean passed;
    private float firstResponseTime;
    private float avgResponseTime;
    private float resolutionTime;
    private String summary;
    private List<CriteriaDTO> criterias;
}