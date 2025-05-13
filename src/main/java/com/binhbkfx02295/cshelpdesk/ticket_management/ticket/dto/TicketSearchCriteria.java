package com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto;

import com.binhbkfx02295.cshelpdesk.ticket_management.category.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketSearchCriteria {
    private String assignee;           // assignee
    private String facebookUserId;
    private String title;
    private String tag;
    private String progressStatus;
    private Timestamp fromDate;
    private Timestamp toDate;
    private String category;
    private String emotion;
    private String satisfaction;
}
