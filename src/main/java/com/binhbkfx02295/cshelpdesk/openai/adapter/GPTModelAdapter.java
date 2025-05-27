package com.binhbkfx02295.cshelpdesk.openai.adapter;

import com.binhbkfx02295.cshelpdesk.message.entity.Message;
import com.binhbkfx02295.cshelpdesk.openai.model.GPTResult;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import java.util.List;


public interface GPTModelAdapter {
    public GPTResult analyze(List<Message> messages);
}