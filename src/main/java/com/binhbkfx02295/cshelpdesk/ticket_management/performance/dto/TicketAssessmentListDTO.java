package com.binhbkfx02295.cshelpdesk.ticket_management.performance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.scheduling.annotation.Async;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketAssessmentListDTO {
    private long ticketId;
    private String assigneeUsername;
    private String evaluatedBy;
    private long evaluatedAt;
    private boolean isPassed;

}
