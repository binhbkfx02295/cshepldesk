package com.binhbkfx02295.cshelpdesk.websocket.listener;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.EmployeeTicketDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee;
import com.binhbkfx02295.cshelpdesk.infrastructure.common.cache.MasterDataCache;
import com.binhbkfx02295.cshelpdesk.websocket.event.EmployeeEvent;
import com.binhbkfx02295.cshelpdesk.websocket.event.MessageEvent;
import com.binhbkfx02295.cshelpdesk.websocket.event.TicketAssignedEvent;
import com.binhbkfx02295.cshelpdesk.websocket.event.TicketEvent;
import com.binhbkfx02295.cshelpdesk.websocket.dto.NotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {
    private final MasterDataCache cache;
    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void onTicketEvent(TicketEvent event) {
        EmployeeTicketDTO assignee = event.getTicket().getAssignee();
        if (assignee != null) {
            if (assignee.getGroup().getCode().equalsIgnoreCase("staff")) {
                messagingTemplate.convertAndSendToUser(assignee.getUsername(), "/queue/tickets",
                        new NotificationDTO<>("TICKET", event.getAction().name(), event.getTicket()));
            }
        } else {
            for (Employee employee: cache.getAllEmployees().values()) {
                messagingTemplate.convertAndSendToUser(employee.getUsername(), "/queue/tickets",
                        new NotificationDTO<>("TICKET", event.getAction().name(), event.getTicket()));
            }
        }

        messagingTemplate.convertAndSend("/topic/admin/tickets",
                new NotificationDTO<>("TICKET", event.getAction().name(), event.getTicket()));

    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMessageEvent(MessageEvent event) {

        EmployeeTicketDTO assignee = event.getMessage().getTicket().getAssignee();
        if (assignee != null && assignee.getGroup() != null) {
            if (assignee.getGroup().getCode().equalsIgnoreCase("staff")) {
                messagingTemplate.convertAndSendToUser(assignee.getUsername(), "/queue/messages",
                        new NotificationDTO<>("MESSAGE", "CREATED", event.getMessage()));
            }
        } else {
            for (Employee employee: cache.getAllEmployees().values()) {
                messagingTemplate.convertAndSendToUser(employee.getUsername(), "/queue/messages",
                        new NotificationDTO<>("MESSAGE", "CREATED", event.getMessage()));
            }
        }
        messagingTemplate.convertAndSend("/topic/admin/messages",
                new NotificationDTO<>("MESSAGE","CREATED", event.getMessage()));

    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEmployeeEvent(EmployeeEvent event) {
        // Broadcast EMPLOYEE changes to admins only
        messagingTemplate.convertAndSend("/topic/admin/employees",
                new NotificationDTO<>("EMPLOYEE", event.getAction().name(), event.getEmployeeDTO()));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTicketAssignedEvent(TicketAssignedEvent event) {
        for (Employee employee: cache.getAllEmployees().values()) {
            if (!employee.getUsername().equalsIgnoreCase(event.getTicket().getAssignee().getUsername())) {
                messagingTemplate.convertAndSendToUser(employee.getUsername(), "/queue/tickets",
                        new NotificationDTO<>("TICKET", "ASSIGNED", event.getTicket()));
            }

        }
    }
}
