package com.binhbkfx02295.cshelpdesk.employee_management.employee.dto;

import com.binhbkfx02295.cshelpdesk.employee_management.usergroup.UserGroupDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDashboardDTO {
    private UserGroupDTO userGroup;
    private String name;
    private String username;
    private String password;
    private String description;
    private boolean isActive;
    private int failedLoginCount;
    private List<StatusLogDTO> statusLog;
}
