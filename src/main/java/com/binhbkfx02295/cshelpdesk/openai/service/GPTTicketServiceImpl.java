package com.binhbkfx02295.cshelpdesk.openai.service;

import com.binhbkfx02295.cshelpdesk.message.entity.Message;
import com.binhbkfx02295.cshelpdesk.openai.adapter.BaseGPTModelAdapter;
import com.binhbkfx02295.cshelpdesk.openai.adapter.GPT41NanoAdapter;
import com.binhbkfx02295.cshelpdesk.openai.adapter.GPTModelAdapter;
import com.binhbkfx02295.cshelpdesk.openai.model.GPTResult;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GPTTicketServiceImpl implements GPTTicketService{

    private final GPT41NanoAdapter adapter;

    @Override
    public GPTResult analyze(Ticket ticket) {
        return analyze(ticket.getMessages());
    }

    @Override
    public GPTResult analyze(List<Message> messages) {
        return adapter.analyze(messages);
    }
}
