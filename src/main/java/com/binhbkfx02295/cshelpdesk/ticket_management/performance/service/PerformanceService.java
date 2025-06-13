package com.binhbkfx02295.cshelpdesk.ticket_management.performance.service;

import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.dto.CriteriaDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.dto.PerformanceSummaryDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.dto.TicketAssessmentDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.dto.TicketAssessmentDetailDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketPerformanceDTO;

import java.time.ZoneId;
import java.util.List;

public interface PerformanceService {

    APIResultSet<PerformanceSummaryDTO> getMonthlyReport(String username, int month, ZoneId zone);

    APIResultSet<PerformanceSummaryDTO> getChatGPTSummary(String username, int month, ZoneId zone);

    //Lay tickets tu TicketService, dung chatGPT evaluate, luu database
    APIResultSet<Void> evaluateTickets();

    APIResultSet<String> buildPrompt();

    APIResultSet<TicketAssessmentDetailDTO> getTicketAssessment(Long id);

    APIResultSet<TicketAssessmentDetailDTO> updateTicketAssessment(Long id, TicketAssessmentDetailDTO dto);
}
