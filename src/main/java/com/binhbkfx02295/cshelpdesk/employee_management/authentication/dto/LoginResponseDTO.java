package com.binhbkfx02295.cshelpdesk.employee_management.authentication.dto;

import com.binhbkfx02295.cshelpdesk.employee_management.authentication.util.ValidationResult;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.EmployeeDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.permission.PermissionDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.usergroup.UserGroupDTO;
import lombok.Data;

import java.util.Set;

@Data
public class LoginResponseDTO {
    private EmployeeDTO employee;
    private UserGroupDTO group;
    private ValidationResult validationResult;
    private Set<PermissionDTO> permissions;
}