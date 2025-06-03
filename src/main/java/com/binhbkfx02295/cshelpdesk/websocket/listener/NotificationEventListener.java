package com.binhbkfx02295.cshelpdesk.websocket.listener;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.EmployeeTicketDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee;
import com.binhbkfx02295.cshelpdesk.infrastructure.common.cache.MasterDataCache;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDashboardDTO;
import com.binhbkfx02295.cshelpdesk.websocket.event.EmployeeEvent;
import com.binhbkfx02295.cshelpdesk.websocket.event.MessageEvent;
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
        log.debug("Publishing TicketEvent {} for ticket {}", event.getAction(), event.getTicket().getId());
        EmployeeTicketDTO assignee = event.getTicket().getAssignee();
        if (assignee != null) {
            log.info(assignee.toString());
            if (assignee.getGroup() != null && assignee.getGroup().getGroupId() == 1) {
                messagingTemplate.convertAndSendToUser(assignee.getUsername(), "/queue/tickets",
                        new NotificationDTO<>("TICKET", event.getAction().name(), event.getTicket()));
            }
        } else {
            messagingTemplate.convertAndSend("/topic/admin/tickets",
                    new NotificationDTO<>("TICKET", event.getAction().name(), event.getTicket()));
        }

    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMessageEvent(MessageEvent event) {
        log.info("Publishing MessageEvent {} for message {}", event.getMessage().getId(), event.getMessage());

        EmployeeTicketDTO assignee = event.getMessage().getTicket().getAssignee();
        if (assignee != null && assignee.getGroup() != null) {
            log.info(assignee.toString());
            if (assignee.getGroup().getGroupId() == 1) {
                messagingTemplate.convertAndSendToUser(assignee.getUsername(), "/queue/messages",
                        new NotificationDTO<>("MESSAGE", "CREATED", event.getMessage()));

            }
        } else {
            messagingTemplate.convertAndSend("/topic/admin/messages",
                    new NotificationDTO<>("MESSAGE","CREATED", event.getMessage()));
        }

    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEmployeeEvent(EmployeeEvent event) {
        log.debug("Publishing EmployeeEvent {} for employee {}", event.getAction(), event.getEmployeeDTO());

        // Broadcast EMPLOYEE changes to admins only
        messagingTemplate.convertAndSend("/topic/admin/employees",
                new NotificationDTO<>("EMPLOYEE", event.getAction().name(), event.getEmployeeDTO()));
    }
}
