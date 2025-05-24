package com.binhbkfx02295.cshelpdesk.employee_management.employee.mapper;

import com.binhbkfx02295.cshelpdesk.employee_management.permission.PermissionMapper;
import com.binhbkfx02295.cshelpdesk.employee_management.usergroup.UserGroup;
import com.binhbkfx02295.cshelpdesk.employee_management.usergroup.UserGroupDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserGroupMapper {

    private final PermissionMapper permissionMapper;

    public UserGroup toEntity(UserGroupDTO dto) {
        if (dto == null) {
            return null;
        }
        UserGroup entity = new UserGroup();
        entity.setGroupId(entity.getGroupId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());

        return entity;
    }

    public UserGroupDTO toDTO(UserGroup entity) {
        if (entity == null) return null;
        UserGroupDTO dto = new UserGroupDTO();
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setGroupId(entity.getGroupId());
        if (entity.getPermissions() != null) {
            entity.getPermissions().forEach(permission -> dto.getPermissions().add(permissionMapper.toDTO(permission)));
        }
        return dto;
    }
}
