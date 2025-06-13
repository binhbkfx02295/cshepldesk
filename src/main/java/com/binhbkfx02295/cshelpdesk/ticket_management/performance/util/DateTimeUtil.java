package com.binhbkfx02295.cshelpdesk.ticket_management.performance.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;

public class DateTimeUtil {

    public static Timestamp[] getStartEndOfMonth(int month, ZoneId zoneId) {
        int year = LocalDateTime.now(zoneId).getYear();

        YearMonth ym = YearMonth.of(year, month);

        LocalDateTime startOfMonth = ym.atDay(1).atStartOfDay();

        LocalDateTime endOfMonth = ym.atEndOfMonth().atTime(23, 59, 59, 999_999_999);

        Timestamp startTimestamp = Timestamp.from(startOfMonth.atZone(zoneId).toInstant());
        Timestamp endTimestamp = Timestamp.from(endOfMonth.atZone(zoneId).toInstant());

        return new Timestamp[] { startTimestamp, endTimestamp };
    }

    public static String formatDuration(double totalSeconds) {
        int seconds = (int) Math.round(totalSeconds);
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;

        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours).append(" giờ ");
        }
        if (minutes > 0 || hours > 0) { // in phút nếu phút > 0 hoặc có giờ
            sb.append(minutes).append(" phút ");
        }
        sb.append(secs).append(" giây");

        return sb.toString().trim();
    }

}