package com.binhbkfx02295.cshelpdesk.ticket_management.performance.controller;

import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResponseEntityHelper;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.dto.CriteriaDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.dto.CriteriaDetailDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.dto.PerformanceSummaryDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.dto.TicketAssessmentDetailDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.service.CriteriaService;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/performance")
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceService performanceService;
    private final CriteriaService criteriaService;

    @GetMapping(params = {"username", "month", "timezone"}) //month starting at 0
    public ResponseEntity<APIResultSet<PerformanceSummaryDTO>> getReportByMonth(
            String username, int month, String timezone) {
        return APIResponseEntityHelper.from(performanceService.getMonthlyReport(username, month, ZoneId.of(timezone)));
    }

    @GetMapping(value = "chat-summary", params = {"username", "month", "timezone"}) //month starting at 0
    public ResponseEntity<APIResultSet<PerformanceSummaryDTO>> getChatGPTSummary(
            String username, int month, String timezone) {
        return APIResponseEntityHelper.from(performanceService.getChatGPTSummary(username, month, ZoneId.of(timezone)));
    }

    @GetMapping("/ticket-assessment/{id}")
    public ResponseEntity<APIResultSet<TicketAssessmentDetailDTO>> getTicketAssessment(
            @PathVariable("id") Long id) {
        return APIResponseEntityHelper.from(performanceService.getTicketAssessment(id));
    }

    @PutMapping("/ticket-assessment/{id}")
    public ResponseEntity<APIResultSet<TicketAssessmentDetailDTO>> updateTicketAssessment(
            @PathVariable("id") Long id, @RequestBody TicketAssessmentDetailDTO dto) {
        return APIResponseEntityHelper.from(performanceService.updateTicketAssessment(id, dto));
    }


    @GetMapping("/criteria")
    public ResponseEntity<APIResultSet<List<CriteriaDTO>>> getCriterias() {
        return APIResponseEntityHelper.from(criteriaService.findAll());
    }

    @GetMapping("/criteria/{id}")
    public ResponseEntity<APIResultSet<CriteriaDetailDTO>> getCriteria(@PathVariable long id) {
        return APIResponseEntityHelper.from(criteriaService.findById(id));
    }

    @PostMapping("/criteria")
    public ResponseEntity<APIResultSet<CriteriaDetailDTO>> createCriteria(@RequestBody CriteriaDetailDTO dto) {
        return APIResponseEntityHelper.from(criteriaService.create(dto));
    }

    @PutMapping("/criteria")
    public ResponseEntity<APIResultSet<CriteriaDetailDTO>> updateCriteria(@RequestBody CriteriaDetailDTO dto) {
        return APIResponseEntityHelper.from(criteriaService.update(dto.getId(), dto));
    }

    @DeleteMapping("/criteria/{id}")
    public ResponseEntity<APIResultSet<Void>> deleteCriteria(@PathVariable long id) {
        return APIResponseEntityHelper.from(criteriaService.delete(id));
    }

    @GetMapping("/criteria/buildPrompt")
    public ResponseEntity<APIResultSet<String>> buildPrompt() {
        return APIResponseEntityHelper.from(performanceService.buildPrompt());
    }

    @GetMapping("/evaluateTicket")
    public ResponseEntity<APIResultSet<Void>> evaluateTickets() {
        return APIResponseEntityHelper.from(performanceService.evaluateTickets());
    }

}
