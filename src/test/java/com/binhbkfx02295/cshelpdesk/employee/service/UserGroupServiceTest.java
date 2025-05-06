package com.binhbkfx02295.cshelpdesk.employee.service;

import com.binhbkfx02295.cshelpdesk.employee_management.usergroup.UserGroupDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.permission.Permission;
import com.binhbkfx02295.cshelpdesk.employee_management.usergroup.UserGroup;
import com.binhbkfx02295.cshelpdesk.employee_management.permission.PermissionRepository;
import com.binhbkfx02295.cshelpdesk.employee_management.usergroup.UserGroupRepository;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.repository.EmployeeRepository;
import com.binhbkfx02295.cshelpdesk.employee_management.usergroup.UserGroupServiceImpl;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserGroupServiceTest {

    @Mock
    private UserGroupRepository userGroupRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private UserGroupServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createGroup_ShouldReturnOkResult() {
        UserGroupDTO dto = new UserGroupDTO();
        dto.setName("staff");
        dto.setPermissionIds(Set.of(1, 2));

        Permission p1 = new Permission(); p1.setId(1);
        Permission p2 = new Permission(); p2.setId(2);

        UserGroup saved = new UserGroup();
        saved.setGroupId(10);
        saved.setName("staff");
        saved.setPermissions(Set.of(p1, p2));

        when(permissionRepository.findAllById(dto.getPermissionIds())).thenReturn(List.of(p1, p2));
        when(userGroupRepository.save(any(UserGroup.class))).thenReturn(saved);

        APIResultSet<UserGroupDTO> result = service.createGroup(dto);

        assertEquals(200, result.getHttpCode());
        assertEquals("staff", result.getData().getName());
        assertEquals(Set.of(1, 2), result.getData().getPermissionIds());
    }

    @Test
    void getGroupById_ShouldReturnOkResult() {
        UserGroup group = new UserGroup();
        group.setGroupId(1);
        group.setName("Group A");

        Permission p = new Permission(); p.setId(5);
        group.setPermissions(Set.of(p));

        when(userGroupRepository.findById(1)).thenReturn(Optional.of(group));

        APIResultSet<UserGroupDTO> result = service.getGroupById("1");

        assertEquals(200, result.getHttpCode());
        assertEquals("Group A", result.getData().getName());
    }

    @Test
    void updateGroup_ShouldReturnOkResult() {
        UserGroup existing = new UserGroup();
        existing.setGroupId(1);
        existing.setName("old");

        UserGroupDTO dto = new UserGroupDTO();
        dto.setName("new");
        dto.setPermissionIds(Set.of(3));

        Permission p = new Permission(); p.setId(3);

        when(userGroupRepository.findById(1)).thenReturn(Optional.of(existing));
        when(permissionRepository.findAllById(Set.of(3))).thenReturn(List.of(p));
        when(userGroupRepository.save(any(UserGroup.class))).thenAnswer(i -> i.getArgument(0));

        APIResultSet<UserGroupDTO> result = service.updateGroup("1", dto);

        assertEquals(200, result.getHttpCode());
        assertEquals("new", result.getData().getName());
    }

    @Test
    void deleteGroup_ShouldReturnOkWhenExists() {
        // Arrange
        UserGroup mockGroup = new UserGroup();
        mockGroup.setGroupId(1);
        mockGroup.setName("Staff");

        when(userGroupRepository.findById(1)).thenReturn(Optional.of(mockGroup));
        when(employeeRepository.findByUserGroup_GroupId(1)).thenReturn(Collections.emptyList()); // Không có user liên quan

        // Act
        APIResultSet<Void> result = service.deleteGroup("1");

        // Assert
        assertEquals(200, result.getHttpCode());
        assertNull(result.getData());
        verify(employeeRepository).findByUserGroup_GroupId(1);
        verify(employeeRepository).saveAll(Collections.emptyList());
        verify(userGroupRepository).deleteById(1);
    }


    @Test
    void getAllGroups_ShouldReturnList() {
        UserGroup g = new UserGroup();
        g.setGroupId(1);
        g.setName("test");

        Permission p = new Permission(); p.setId(1);
        g.setPermissions(Set.of(p));

        when(userGroupRepository.findAll()).thenReturn(List.of(g));

        APIResultSet<List<UserGroupDTO>> result = service.getAllGroups();

        assertEquals(200, result.getHttpCode());
        assertEquals(1, result.getData().size());
        assertEquals("test", result.getData().get(0).getName());
    }

    @Test
    void getGroupById_NotFound_ShouldReturn404() {
        when(userGroupRepository.findById(999)).thenReturn(Optional.empty());

        APIResultSet<UserGroupDTO> result = service.getGroupById("999");

        assertEquals(404, result.getHttpCode());
        assertNull(result.getData());
    }
}
