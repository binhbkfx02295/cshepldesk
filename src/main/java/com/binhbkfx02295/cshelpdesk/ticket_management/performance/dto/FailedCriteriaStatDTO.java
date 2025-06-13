package com.binhbkfx02295.cshelpdesk.ticket_management.performance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FailedCriteriaStatDTO {
    private String code;
    private String name;
    private String description;
    private long   count;       // số ticket vi phạm criteria này
}