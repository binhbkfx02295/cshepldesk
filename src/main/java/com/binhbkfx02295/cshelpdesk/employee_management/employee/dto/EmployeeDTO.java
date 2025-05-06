package com.binhbkfx02295.cshelpdesk.employee_management.employee.dto;

import lombok.Data;

import java.util.List;

@Data
public class EmployeeDTO {
    private int groupId;
    private String name;
    private String username;
    private String password;
    private String description;
    private boolean isActive;
    private int failedLoginCount;
    private List<StatusLogDTO> statusLog;

}
