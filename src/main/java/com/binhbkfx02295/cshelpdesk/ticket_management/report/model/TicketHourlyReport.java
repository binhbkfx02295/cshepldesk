package com.binhbkfx02295.cshelpdesk.ticket_management.report.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TicketHourlyReport {
    private List<Integer> labels = new ArrayList<>(24);
    private List<Integer> data = new ArrayList<>(24);
    {
        for (int i = 0; i < 24; i++) {
            labels.add(i);
            data.add(0); // khởi tạo giá trị đếm ban đầu là 0
        }
    }
}
