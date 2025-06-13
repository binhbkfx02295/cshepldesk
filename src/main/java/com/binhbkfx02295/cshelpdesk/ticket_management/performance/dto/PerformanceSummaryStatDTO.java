package com.binhbkfx02295.cshelpdesk.ticket_management.performance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceSummaryStatDTO {
    private Map<String, Object> chatQuality;
    private Map<String, Object> firstResponseTime; // seconds
    private Map<String, Object> avgResponseTime;   // seconds
    private Map<String, Object> resolutionTime;    // seconds

    private String chatGPTsummary;
}
