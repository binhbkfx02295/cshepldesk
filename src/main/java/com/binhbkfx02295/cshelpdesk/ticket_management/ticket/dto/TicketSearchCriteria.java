package com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketSearchCriteria {
    private String assignee;           // assignee
    private String facebookId;
    private String title;
    private String tag;
    private String progressStatus;
    private Long fromTime;
    private Long toTime;
    private String category;
    private String emotion;
    private String satisfaction;
}
