package com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.EmployeeTicketDTO;
import com.binhbkfx02295.cshelpdesk.message.dto.MessageDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.dto.SatisfactionDTO;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data

public class TicketPerformanceDTO {
    private int id;
    private Timestamp createdAt;
    private EmployeeTicketDTO assignee;
    private List<MessageDTO> messages;
}
