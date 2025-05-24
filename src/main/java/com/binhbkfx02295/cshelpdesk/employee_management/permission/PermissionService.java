package com.binhbkfx02295.cshelpdesk.employee_management.permission;

import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;

import java.util.List;

public interface PermissionService {

    APIResultSet<PermissionDTO> createPermission(PermissionDTO permissionDTO);
    APIResultSet<PermissionDTO> updatePermission(String permissionId, PermissionDTO permissionDTO);
    APIResultSet<Boolean> deletePermission(String permissionId);
    APIResultSet<PermissionDTO> getPermissionById(String permissionId);
    APIResultSet<List<PermissionDTO>> getAllPermissions();
}
