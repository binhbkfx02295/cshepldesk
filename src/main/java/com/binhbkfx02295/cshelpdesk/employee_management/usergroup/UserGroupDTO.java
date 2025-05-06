package com.binhbkfx02295.cshelpdesk.employee_management.usergroup;

import lombok.Data;

import java.util.Set;

@Data
public class UserGroupDTO {
    private int groupId;
    private String name;
    private Set<Integer> permissionIds;
    private String description;
}