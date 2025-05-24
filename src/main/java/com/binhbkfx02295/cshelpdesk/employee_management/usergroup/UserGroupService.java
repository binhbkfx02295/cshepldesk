package com.binhbkfx02295.cshelpdesk.employee_management.usergroup;

import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;

import java.util.List;

public interface UserGroupService {
    APIResultSet<UserGroupDTO> createGroup(UserGroupDTO groupDTO);
    APIResultSet<UserGroupDTO> updateGroup(String groupId, UserGroupDTO groupDTO);
    APIResultSet<Void> deleteGroup(String groupId);
    APIResultSet<UserGroupDTO> getGroupById(String groupId);
    APIResultSet<List<UserGroupDTO>> getAllGroups();
}
