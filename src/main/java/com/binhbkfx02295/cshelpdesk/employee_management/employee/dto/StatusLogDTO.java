package com.binhbkfx02295.cshelpdesk.employee_management.employee.dto;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Status;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.StatusLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusLogDTO {
    private String username;
    private String status;
    private Timestamp from;

    public StatusLog toEntity() {
        StatusLog log = new StatusLog();
        log.setStatus(new Status(0,status, null));
        return log;
    }
}
