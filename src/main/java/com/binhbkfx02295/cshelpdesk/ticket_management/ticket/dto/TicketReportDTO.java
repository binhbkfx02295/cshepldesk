package com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto;

import com.binhbkfx02295.cshelpdesk.message.dto.MessageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketReportDTO {
    private int id;
    private String username;
    private String name;
    private long createdAt;
    private long closedAt;
    private float firstResponseTime;
    private float avgResponseTime;
    private float resolutionTime;
    private List<MessageDTO> messages = new ArrayList<>();
}
