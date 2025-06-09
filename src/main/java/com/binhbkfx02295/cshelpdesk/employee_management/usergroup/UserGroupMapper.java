package com.binhbkfx02295.cshelpdesk.employee_management.usergroup;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserGroupMapper {

    public UserGroup toEntity(UserGroupDTO dto) {
        if (dto == null) {
            return null;
        }
        UserGroup entity = new UserGroup();
        entity.setGroupId(entity.getGroupId());
        entity.setName(dto.getName());
        entity.setCode(dto.getCode());
        entity.setDescription(dto.getDescription());

        return entity;
    }

    public UserGroupDTO toDTO(UserGroup entity) {
        if (entity == null) return null;
        UserGroupDTO dto = new UserGroupDTO();
        dto.setName(entity.getName());
        dto.setCode(entity.getCode());
        dto.setDescription(entity.getDescription());
        dto.setGroupId(entity.getGroupId());
        return dto;
    }
}
