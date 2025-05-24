package com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketReportDTO {
    private int id;
    private Long firstResponseRate;
    private Long overallResponseRate;
    private Long resolutionRate;
    private Timestamp createdAt;
}
