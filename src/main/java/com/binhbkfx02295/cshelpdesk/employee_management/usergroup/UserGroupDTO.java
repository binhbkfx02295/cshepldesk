package com.binhbkfx02295.cshelpdesk.employee_management.usergroup;

import com.binhbkfx02295.cshelpdesk.employee_management.permission.PermissionDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserGroupDTO {
    private int groupId;
    private String name;
    private List<PermissionDTO> permissions = new ArrayList<>();
    private String description;
}