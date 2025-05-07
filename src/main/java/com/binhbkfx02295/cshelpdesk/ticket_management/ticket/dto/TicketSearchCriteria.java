package com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto;

import com.binhbkfx02295.cshelpdesk.ticket_management.category.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private String category;
    private int emotion;
    private int satisfaction;
}
