package com.binhbkfx02295.cshelpdesk.websocket.event;

import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDashboardDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TicketAssignedEvent {
    private final TicketDashboardDTO ticket;
}
