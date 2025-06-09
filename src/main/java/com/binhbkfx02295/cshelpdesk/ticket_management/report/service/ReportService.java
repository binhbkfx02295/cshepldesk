package com.binhbkfx02295.cshelpdesk.ticket_management.report.service;

import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.ticket_management.report.model.Report;
import com.binhbkfx02295.cshelpdesk.ticket_management.report.model.TicketDailyReport;
import com.binhbkfx02295.cshelpdesk.ticket_management.report.model.TicketHourlyReport;
import com.binhbkfx02295.cshelpdesk.ticket_management.report.model.TicketWeekdayReport;

import java.sql.Timestamp;

public interface ReportService {

    APIResultSet<Report> fetchHourlyReport(long fromTime, long toTime, String type, String label, boolean main, String timezone);
    APIResultSet<Report> fetchWeekdayReport(long fromTime, long toTime, String type, String label, boolean main, String timezone);
    APIResultSet<Report> fetchDayInMonthReport(long fromTime, long toTime, String type, String label, boolean main, String timezone);
}
