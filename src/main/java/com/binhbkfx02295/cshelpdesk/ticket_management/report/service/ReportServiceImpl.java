package com.binhbkfx02295.cshelpdesk.ticket_management.report.service;

import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.ticket_management.report.model.TicketDailyReport;
import com.binhbkfx02295.cshelpdesk.ticket_management.report.model.TicketHourlyReport;
import com.binhbkfx02295.cshelpdesk.ticket_management.report.model.TicketWeekdayReport;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketVolumeReportDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final TicketService ticketService;

    @Override
    public APIResultSet<TicketHourlyReport> fetchHourlyReport(long fromTime, long toTime) {
        return generateReport(fromTime, toTime, this::toHourlyReport);
    }

    @Override
    public APIResultSet<TicketWeekdayReport> fetchWeekdayReport(long fromTime, long toTime) {
        return generateReport(fromTime, toTime, this::toWeekdayReport);
    }

    @Override
    public APIResultSet<TicketDailyReport> fetchDailyReport(long fromTime, long toTime) {
        return generateReport(fromTime, toTime, this::toDailyReport);
    }

    private <T> APIResultSet<T> generateReport(long fromTime, long toTime, Function<List<TicketVolumeReportDTO>, T> reportMapper) {
        try {
            Timestamp from = new Timestamp(fromTime);
            Timestamp to = new Timestamp(toTime);

            APIResultSet<List<TicketVolumeReportDTO>> resultSet = ticketService.searchTicketsForVolumeReport(from, to);

            if (!resultSet.isSuccess()) {
                log.warn("Failed to fetch ticket data: {}", resultSet.getMessage());
                return APIResultSet.internalError("Không thể tạo báo cáo: " + resultSet.getMessage());
            }

            T report = reportMapper.apply(resultSet.getData());
            return APIResultSet.ok("Tạo báo cáo thành công", report);
        } catch (Exception e) {
            log.error("Lỗi khi tạo báo cáo", e);
            return APIResultSet.internalError("Lỗi hệ thống khi tạo báo cáo");
        }
    }

    private TicketHourlyReport toHourlyReport(List<TicketVolumeReportDTO> data) {
        TicketHourlyReport report = new TicketHourlyReport();
        data.forEach(ticket -> {
            int hour = ticket.getCreatedAt().toLocalDateTime().getHour();
            if (hour >= 0 && hour < report.getData().size()) {
                report.getData().set(hour, report.getData().get(hour) + 1);
            }
        });
        return report;
    }

    private TicketWeekdayReport toWeekdayReport(List<TicketVolumeReportDTO> data) {
        TicketWeekdayReport report = new TicketWeekdayReport();
        data.forEach(ticket -> {
            int dayOfWeek = ticket.getCreatedAt().toLocalDateTime().getDayOfWeek().getValue() % 7; // Convert Sunday to 0
            if (dayOfWeek >= 0 && dayOfWeek < report.getData().size()) {
                report.getData().set(dayOfWeek, report.getData().get(dayOfWeek) + 1);
            }
        });
        return report;
    }

    private TicketDailyReport toDailyReport(List<TicketVolumeReportDTO> data) {
        TicketDailyReport report = new TicketDailyReport();
        data.forEach(ticket -> {
            LocalDateTime dateTime = ticket.getCreatedAt().toLocalDateTime();
            int day = dateTime.getDayOfMonth() - 1; // Day starts from 1
            if (day >= 0 && day < report.getData().size()) {
                report.getData().set(day, report.getData().get(day) + 1);
            }
        });
        return report;
    }
}
