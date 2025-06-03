package com.binhbkfx02295.cshelpdesk.websocket.dto;

import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public record NotificationDTO<T>(
        String entity,        // "TICKET", "MESSAGE", "EMPLOYEE"
        String action,        // "CREATED", "UPDATED", "DELETED"
        T data                // DTO payload to stringify at client side
) { }