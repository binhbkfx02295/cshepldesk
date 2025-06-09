package com.binhbkfx02295.cshelpdesk.ticket_management.report.service;

import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.ticket_management.report.model.Report;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketVolumeReportDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final TicketService ticketService;

    @Override
    public APIResultSet<Report> fetchHourlyReport(long fromTime, long toTime, String type, String label, boolean main, String timezone) {
        try {

            // 1. Lấy data từ service
            APIResultSet<List<TicketVolumeReportDTO>> resultSet = ticketService.searchTicketsForVolumeReport(
                    new Timestamp(fromTime),
                    new Timestamp(toTime)
            );

            if (!resultSet.isSuccess()) {
                log.warn("Failed to fetch ticket data: {}", resultSet.getMessage());
                return APIResultSet.internalError("Không thể tạo báo cáo: " + resultSet.getMessage());
            }

            // 2. Convert millis sang LocalDate (giữ đúng zone cho VN)
            ZoneId zone = ZoneId.of(timezone);
            LocalDate fromDate = Instant.ofEpochMilli(fromTime).atZone(zone).toLocalDate();
            LocalDate toDate = Instant.ofEpochMilli(toTime).atZone(zone).toLocalDate();

            // 3. Truyền fromDate, toDate vào hàm toHourlyReport đã chuẩn hóa
            Report report = toHourlyReport(resultSet.getData(), fromDate, toDate, type, label, main, zone);
            return APIResultSet.ok("Tạo báo cáo thành công", report);

        } catch (Exception e) {
            log.error("Lỗi khi tạo báo cáo hourly", e);
            return APIResultSet.internalError("Lỗi hệ thống khi tạo báo cáo hourly");
        }
    }

    @Override
    public APIResultSet<Report> fetchWeekdayReport(long fromTime, long toTime, String type, String label, boolean main, String timezone) {
        APIResultSet<Report> result;
        try {
            ZoneId zone = ZoneId.of(timezone);
            LocalDate fromDate = Instant.ofEpochMilli(fromTime).atZone(zone).toLocalDate();
            LocalDate toDate = Instant.ofEpochMilli(toTime).atZone(zone).toLocalDate();

            APIResultSet<List<TicketVolumeReportDTO>> resultSet = ticketService.searchTicketsForVolumeReport(
                    new Timestamp(fromTime),
                    new Timestamp(toTime)
            );

            if (!resultSet.isSuccess()) {
                log.warn("Failed to fetch ticket data: {}", resultSet.getMessage());
                result = APIResultSet.internalError("Không thể tạo báo cáo: " + resultSet.getMessage());
            } else {
                Report report = toWeekdayReport(resultSet.getData(), fromDate, toDate, type, label, main, zone);
                result = APIResultSet.ok("Tạo báo cáo thành công", report);
            }


        } catch (Exception e) {
            log.error("Lỗi khi tạo báo cáo Week day", e);
            result = APIResultSet.internalError("Lỗi hệ thống khi tạo báo cáo Week day");
        }

        log.info(result.getMessage());
        return result;
    }


    @Override
    public APIResultSet<Report> fetchDayInMonthReport(long fromTime, long toTime, String type, String label, boolean main, String timezone) {
        APIResultSet<Report> result;
        try {
            ZoneId zone = ZoneId.of(timezone);
            LocalDate fromDate = Instant.ofEpochMilli(fromTime).atZone(zone).toLocalDate();
            LocalDate toDate = Instant.ofEpochMilli(toTime).atZone(zone).toLocalDate();

            APIResultSet<List<TicketVolumeReportDTO>> resultSet = ticketService.searchTicketsForVolumeReport(
                    new Timestamp(fromTime),
                    new Timestamp(toTime)
            );

            if (!resultSet.isSuccess()) {
                log.warn("Failed to fetch ticket data: {}", resultSet.getMessage());
                result = APIResultSet.internalError("Không thể tạo báo cáo: " + resultSet.getMessage());
            } else {
                Report report = toDailyReport(resultSet.getData(), fromDate, toDate, type, label, main, zone);
                result= APIResultSet.ok("Tạo báo cáo thành công", report);
            }

        } catch (Exception e) {
            log.error("Lỗi khi tạo báo cáo Day in Month", e);
            result = APIResultSet.internalError("Lỗi hệ thống khi tạo báo cáo Day in Month");
        }
        log.info(result.getMessage());
        return result;
    }

    private Report toHourlyReport(List<TicketVolumeReportDTO> data, LocalDate fromDate, LocalDate toDate, String type, String label, boolean main, ZoneId zone) {
        int totalDays = (int) ChronoUnit.DAYS.between(fromDate, toDate) + 1;

        int[] hourCounts = new int[24];
        for (TicketVolumeReportDTO ticket : data) {
            int hour = ticket.getCreatedAt().toInstant().atZone(zone).getHour();
            hourCounts[hour]++;
        }


        List<Double> avgByHour = new ArrayList<>(24);
        int totalTickets = Arrays.stream(hourCounts).sum();
        for (int h = 0; h < 24; h++) {
            avgByHour.add(totalDays > 0 ? ((double) hourCounts[h]) / totalDays : 0.0);
        }


        double averageTickets = totalDays > 0 ? (double) totalTickets / (totalDays * 24) : 0.0;
        double max = avgByHour.stream().max(Double::compareTo).orElse(0.0);
        double min = avgByHour.stream().min(Double::compareTo).orElse(0.0);

        int maxHour = avgByHour.indexOf(max);
        int minHour = avgByHour.indexOf(min);

        List<String> labels = new ArrayList<>(24);
        for (int h = 0; h < 24; h++) {
            labels.add(String.format("%02d:00", h));
        }

        Report.Dataset dataset = Report.Dataset.builder()
                .label(label)
                .type(type)
                .data(avgByHour.stream().map(Double::intValue).toList())
                .main(main)
                .labels(labels)
                .build();

        Map<String, Object> summary = Map.of(
                "total", totalTickets,
                "avg", averageTickets,
                "max", Map.of("value", max, "hour", String.format("%02d:00", maxHour)),
                "min", Map.of("value", min, "hour", String.format("%02d:00", minHour)),
                "totalDays", totalDays
        );


        List<Object> rows = new ArrayList<>(24);
        for (int h = 0; h < 24; h++) {
            double value = totalTickets > 0 ? (double) hourCounts[h]*100 / totalTickets : 0.0;
            rows.add(List.of(labels.get(h), avgByHour.get(h), value));
        }
        Report.TabularData tabularData = Report.TabularData.builder()
                .columns(List.of("Khung giờ", "Lượng ticket trung bình", "Tỷ lệ %"))
                .rows(rows)
                .build();

        return Report.builder()
                .title("Phân bổ tichet theo giờ")
                .dataset(dataset)
                .summary(summary)
                .tabularData(tabularData)
                .build();
    }

    private Report toWeekdayReport(List<TicketVolumeReportDTO> data, LocalDate fromDate, LocalDate toDate, String type, String label, boolean main, ZoneId zone) {
        int dayBetween = (int) ChronoUnit.DAYS.between(fromDate, toDate) + 1;
        int[] weekdays = new int[7];
        int[] weekdayAppearance = new int[7];
        for (int i=0; i < dayBetween; i++) {
            weekdayAppearance[fromDate.plusDays(i).getDayOfWeek().getValue()-1]++;
        }
        for (TicketVolumeReportDTO ticket : data) {
            int weekday = ticket.getCreatedAt().toInstant().atZone(zone).getDayOfWeek().getValue() - 1;
            weekdays[weekday]++;
        }

        int totalTickets = Arrays.stream(weekdays).sum();
        List<Double> averageByDay = new ArrayList<>(7);
        for (int i = 0; i < 7; i++) {
            averageByDay.add(dayBetween > 0 ? (double) weekdays[i] / weekdayAppearance[i] : 0.0);
        }


        List<String> labels = List.of("Thu 2",
                "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy", "Chủ Nhật");
        Report.Dataset dataset = Report.Dataset.builder()
                .label(label)
                .labels(labels)
                .type(type)
                .main(main)
                .data(averageByDay.stream().map(Double::intValue).toList())
                .build();

        double averageTickets = dayBetween > 0 ? (double) totalTickets / (dayBetween) : 0.0;
        double max = averageByDay.stream().max(Double::compareTo).orElse(0.0);
        double min = averageByDay.stream().min(Double::compareTo).orElse(0.0);
        Map<String, Object> summary = new HashMap<>();

        summary.put("total", totalTickets);
        summary.put("avg", averageTickets);
        summary.put("max", Map.of("value", max, "day", averageByDay.indexOf(max)));
        summary.put("min", Map.of("value", min, "day", averageByDay.indexOf(min)));
        summary.put("totalDay", dayBetween);

        List<Object> rows = new ArrayList<>(7);
        for (int h = 0; h < 7; h++) {
            double value = totalTickets > 0 ? (double) weekdays[h]*100 / totalTickets : 0.0;
            rows.add(List.of(labels.get(h), averageByDay.get(h), value));
        }
        Report.TabularData tabularData = Report.TabularData.builder()
                .columns(List.of("Ngày trong tuần", "Lượng ticket trung bình", "Tỷ lệ %"))
                .rows(rows)
                .build();
        return Report.builder()
                .title("Phân bổ ticket theo tuần")
                .tabularData(tabularData)
                .summary(summary)
                .dataset(dataset)
                .build();
    }

    private Report toDailyReport(List<TicketVolumeReportDTO> data,
                                 LocalDate fromDate, LocalDate toDate, String type,
                                 String label, boolean main, ZoneId zone) {
        List<String> labels = IntStream.rangeClosed(1, 31)
                .mapToObj(i -> String.format("%02d", i))
                .toList();

        int[] countDayAppear = new int[31];
        LocalDate date = fromDate;
        while (!date.isAfter(toDate)) {
            int dayOfMonth = date.getDayOfMonth();
            countDayAppear[dayOfMonth - 1]++;
            date = date.plusDays(1);
        }

        int[] ticketCounts = new int[31];
        for (TicketVolumeReportDTO ticket : data) {
            int day = ticket.getCreatedAt().toInstant().atZone(zone).getDayOfMonth();
            ticketCounts[day - 1]++;
        }

        List<Double> avgTicketsByDay = new ArrayList<>(31);
        for (int i = 0; i < 31; i++) {
            double avg = countDayAppear[i] > 0 ? (double) ticketCounts[i] / countDayAppear[i] : 0.0;
            avgTicketsByDay.add(avg);
        }

        double max = avgTicketsByDay.stream().max(Double::compareTo).orElse(0.0);
        double min = avgTicketsByDay.stream().min(Double::compareTo).orElse(0.0);
        int maxIdx = avgTicketsByDay.indexOf(max);
        int minIdx = avgTicketsByDay.indexOf(min);

        int totalTickets = Arrays.stream(ticketCounts).sum();
        int totalAppear = Arrays.stream(countDayAppear).filter(c -> c > 0).sum();
        double average = totalAppear > 0 ? (double) totalTickets / totalAppear : 0.0;

        Report.Dataset dataset = Report.Dataset.builder()
                .label(label)
                .labels(labels)
                .main(main)
                .type(type)
                .data(avgTicketsByDay.stream().map(Double::intValue).toList())
                .build();

        Map<String, Object> summary = Map.of(
                "avg", average,
                "max", Map.of("value", max, "date", labels.get(maxIdx)),
                "min", Map.of("value", min, "date", labels.get(minIdx)),
                "totalTickets", totalTickets,
                "totalDays", totalAppear
        );

        List<Object> rows = new ArrayList<>(31);
        for (int i = 0; i < 31; i++) {
            double value = avgTicketsByDay.get(i);
            double percent = totalTickets > 0 ? value * 100 / totalTickets : 0.0;
            rows.add(List.of(
                    labels.get(i),
                    value,
                    percent
            ));
        }
        Report.TabularData tabularData = Report.TabularData.builder()
                .columns(List.of("Ngày", "Số ticket", "Tỷ lệ %"))
                .rows(rows)
                .build();

        return Report.builder()
                .title("Phân bổ ticket theo ngày")
                .dataset(dataset)
                .summary(summary)
                .tabularData(tabularData)
                .build();
    }
}
