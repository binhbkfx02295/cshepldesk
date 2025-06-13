package com.binhbkfx02295.cshelpdesk.openai.service;

import com.binhbkfx02295.cshelpdesk.message.entity.Message;
import com.binhbkfx02295.cshelpdesk.openai.adapter.GPT41MiniAdapter;
import com.binhbkfx02295.cshelpdesk.openai.adapter.GPT41NanoAdapter;
import com.binhbkfx02295.cshelpdesk.openai.model.GPTResult;
import com.binhbkfx02295.cshelpdesk.openai.model.TicketEvaluateResult;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.dto.PerformanceSummaryDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketReportDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GPTTicketServiceImpl implements GPTTicketService{

    private final GPT41NanoAdapter adapter;
    private final GPT41MiniAdapter gpt41MiniAdapter;

    @Override
    public GPTResult analyze(Ticket ticket) {
        return analyze(ticket.getMessages());
    }

    @Override
    public GPTResult analyze(List<Message> messages) {
        log.info("ChatGPT analyzing... check model: {}", gpt41MiniAdapter.getModelSettings().toString());
        return gpt41MiniAdapter.analyze(messages);
    }

    @Override
    public TicketEvaluateResult evaluateTicketByBatch(Map<String, Object> object) {
        return gpt41MiniAdapter.evaluateTicketByBatch(object);
    }

    public String analyseStaff(PerformanceSummaryDTO report) {
        return gpt41MiniAdapter.analyseStaff(report);
    }
}
