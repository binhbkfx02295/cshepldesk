package com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketDetailDTO {
    private int id;
    private String title;
    private Timestamp createdAt;
    private Timestamp lastUpdateAt;
    private Timestamp closedAt;

    private String assignee;
    private String progressStatus;
    private String category;
    private int emotion;
    private int satisfaction;

    private String facebookUser;
    private List<String> tags;
}
