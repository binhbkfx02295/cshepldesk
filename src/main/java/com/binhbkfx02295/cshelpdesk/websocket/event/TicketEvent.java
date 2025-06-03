package com.binhbkfx02295.cshelpdesk.websocket.event;

import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDashboardDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TicketEvent {
    public enum Action {CREATED, UPDATED, CLOSED}
    private final TicketDashboardDTO ticket;
    private final Action action;
}
