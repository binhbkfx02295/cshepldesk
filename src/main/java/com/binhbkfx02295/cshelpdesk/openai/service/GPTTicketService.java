package com.binhbkfx02295.cshelpdesk.openai.service;

import com.binhbkfx02295.cshelpdesk.message.entity.Message;
import com.binhbkfx02295.cshelpdesk.openai.model.GPTResult;
import com.binhbkfx02295.cshelpdesk.openai.model.TicketEvaluateResult;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import java.util.List;
import java.util.Map;

public interface GPTTicketService {

    public GPTResult analyze(Ticket ticket);

    public GPTResult analyze(List<Message> messages);

    public TicketEvaluateResult evaluateTicketByBatch(Map<String, Object> object);
}
