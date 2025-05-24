package com.binhbkfx02295.cshelpdesk.ticket_management.report.controller;

import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResponseEntityHelper;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.ticket_management.report.model.TicketHourlyReport;
import com.binhbkfx02295.cshelpdesk.ticket_management.report.model.TicketKPIReport;
import com.binhbkfx02295.cshelpdesk.ticket_management.report.service.ReportServiceImpl;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketSearchCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportServiceImpl reportService;

    @GetMapping(value = "/ticket-hourly", params = {"fromDate", "toDate"})
    public ResponseEntity<APIResultSet<TicketHourlyReport>> getHourlyReport(
            long fromDate,
            long toDate) {
        return APIResponseEntityHelper.from(reportService.fetchHourlyReport(fromDate, toDate));
    }
}
