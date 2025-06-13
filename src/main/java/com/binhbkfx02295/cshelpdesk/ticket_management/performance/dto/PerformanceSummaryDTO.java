package com.binhbkfx02295.cshelpdesk.ticket_management.performance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceSummaryDTO {
    private AssigneeDTO assignee;
    private int month; // 1-12
    private PerformanceSummaryStatDTO summary;
}
