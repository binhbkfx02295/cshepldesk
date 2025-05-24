package com.binhbkfx02295.cshelpdesk.ticket_management.report.service;

import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.ticket_management.report.model.TicketDailyReport;
import com.binhbkfx02295.cshelpdesk.ticket_management.report.model.TicketHourlyReport;
import com.binhbkfx02295.cshelpdesk.ticket_management.report.model.TicketWeekdayReport;

import java.sql.Timestamp;

public interface ReportService {

    APIResultSet<TicketHourlyReport> fetchHourlyReport(long fromTime, long toTime);
    APIResultSet<TicketWeekdayReport> fetchWeekdayReport(long fromTime, long toTime);
    APIResultSet<TicketDailyReport> fetchDailyReport(long fromTime, long toTime);

}
