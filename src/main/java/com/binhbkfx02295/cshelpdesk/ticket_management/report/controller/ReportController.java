package com.binhbkfx02295.cshelpdesk.ticket_management.report.controller;

import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResponseEntityHelper;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.ticket_management.report.model.*;
import com.binhbkfx02295.cshelpdesk.ticket_management.report.service.ReportServiceImpl;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketSearchCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportServiceImpl reportService;

    @GetMapping(value = "/ticket-by-hour", params = {"fromTime", "toTime", "type", "label", "timezone"})
    public ResponseEntity<APIResultSet<Report>> getHourlyReport(
            long fromTime,
            long toTime,
            String type,
            String label,
            String timezone,
            @RequestParam(value = "main", defaultValue = "false") boolean main) {
        return APIResponseEntityHelper.from(reportService.fetchHourlyReport(fromTime, toTime, type, label, main, timezone));
    }

    @GetMapping(value = "/ticket-by-weekday", params = {"fromTime", "toTime", "type", "label", "timezone"})
    public ResponseEntity<APIResultSet<Report>> getWeekdayReport(
            long fromTime,
            long toTime,
            String type,
            String label,
            String timezone,
            @RequestParam(value = "main", defaultValue = "false") boolean main) {
        return APIResponseEntityHelper.from(reportService.fetchWeekdayReport(fromTime, toTime, type, label, main, timezone));
    }

    @GetMapping(value = "/ticket-by-day", params = {"fromTime", "toTime", "type", "label", "timezone"})
    public ResponseEntity<APIResultSet<Report>> getDayInMonthReport(
            long fromTime,
            long toTime,
            String type,
            String label,
            String timezone,
            @RequestParam(value = "main", defaultValue = "false") boolean main) {
        return APIResponseEntityHelper.from(reportService.fetchDayInMonthReport(fromTime, toTime, type, label, main, timezone));
    }
}
