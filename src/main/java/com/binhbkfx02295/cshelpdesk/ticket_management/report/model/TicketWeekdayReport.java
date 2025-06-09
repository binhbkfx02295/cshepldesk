package com.binhbkfx02295.cshelpdesk.ticket_management.report.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TicketWeekdayReport {
    private List<Integer> labels = new ArrayList<>(7);
    private List<Integer> data = new ArrayList<>(7);
    {
        for (int i = 0; i < 7; i++) {
            labels.add(i);
            data.add(0); // khởi tạo giá trị đếm ban đầu là 0
        }
    }
}
