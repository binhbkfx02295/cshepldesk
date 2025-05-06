package com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketDTO {
    private int id;
    private String title;
    private Timestamp createdAt;
    private String assignee;
    private String progressCode;
}
