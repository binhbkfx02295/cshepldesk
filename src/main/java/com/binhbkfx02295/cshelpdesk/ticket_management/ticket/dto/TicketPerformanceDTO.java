package com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.EmployeeTicketDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.dto.SatisfactionDTO;
import lombok.Data;

@Data

public class TicketPerformanceDTO {
    private EmployeeTicketDTO assignee;
    private SatisfactionDTO satisfactionDTO;
    private Long firstResponseRate;
    private Long overallResponseRate;
    private Long resolutionRate;
}
