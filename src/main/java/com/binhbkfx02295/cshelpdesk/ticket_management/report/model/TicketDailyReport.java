package com.binhbkfx02295.cshelpdesk.ticket_management.report.model;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Data
public class TicketDailyReport {
    private List<Integer> labels = new ArrayList<>(31);
    private List<Integer> data = new ArrayList<>(31);
    {
        for (int i = 0; i < 31; i++) {
            labels.add(i);
            data.add(0); // khởi tạo giá trị đếm ban đầu là 0
        }
    }
}
