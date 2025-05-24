package com.binhbkfx02295.cshelpdesk.ticket_management.report.model;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TicketDailyReport {
    private List<Integer> labels = new ArrayList<>(7);
    private List<Integer> data = new ArrayList<>(7);
    {
        for (int i = 0; i < 6; i++) {
            labels.add(i);
            data.add(0); // khởi tạo giá trị đếm ban đầu là 0
        }
    }

}
