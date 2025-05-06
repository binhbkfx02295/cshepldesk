package com.binhbkfx02295.cshelpdesk.employee.service;

import com.binhbkfx02295.cshelpdesk.employee_management.permission.PermissionDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.permission.Permission;
import com.binhbkfx02295.cshelpdesk.employee_management.permission.PermissionRepository;
import com.binhbkfx02295.cshelpdesk.employee_management.permission.PermissionServiceImpl;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PermissionServiceTest {

    private PermissionRepository permissionRepository;
    private PermissionServiceImpl permissionService;

    @BeforeEach
    void setUp() {
        permissionRepository = mock(PermissionRepository.class);
        permissionService = new PermissionServiceImpl(permissionRepository);
    }

    @Test
    void testCreatePermission() {
        PermissionDTO dto = new PermissionDTO();
        dto.setName("VIEW_DASHBOARD");
        dto.setDescription("View dashboard");

        Permission saved = new Permission();
        saved.setId(1);
        saved.setName("VIEW_DASHBOARD");
        saved.setDescription("View dashboard");

        when(permissionRepository.save(any(Permission.class))).thenReturn(saved);

        APIResultSet<PermissionDTO> result = permissionService.createPermission(dto);

        assertEquals(200, result.getHttpCode());
        assertEquals("VIEW_DASHBOARD", result.getData().getName());
        verify(permissionRepository, times(1)).save(any(Permission.class));
    }

    @Test
    void testUpdatePermission_Success() {
        Permission existing = new Permission();
        existing.setId(1);
        existing.setName("OLD_NAME");
        existing.setDescription("OLD_DESC");

        PermissionDTO updateDTO = new PermissionDTO();
        updateDTO.setName("NEW_NAME");
        updateDTO.setDescription("NEW_DESC");

        when(permissionRepository.findById(1)).thenReturn(Optional.of(existing));
        when(permissionRepository.save(any())).thenReturn(existing);

        APIResultSet<PermissionDTO> result = permissionService.updatePermission("1", updateDTO);

        assertEquals(200, result.getHttpCode());
        assertEquals("NEW_NAME", result.getData().getName());
    }

    @Test
    void testUpdatePermission_NotFound() {
        when(permissionRepository.findById(1)).thenReturn(Optional.empty());

        PermissionDTO dto = new PermissionDTO();
        APIResultSet<PermissionDTO> result = permissionService.updatePermission("1", dto);

        assertEquals(404, result.getHttpCode());
    }

    @Test
    void testGetPermissionById_Success() {
        Permission permission = new Permission();
        permission.setId(1);
        permission.setName("TEST");
        permission.setDescription("desc");

        when(permissionRepository.findById(1)).thenReturn(Optional.of(permission));

        APIResultSet<PermissionDTO> result = permissionService.getPermissionById("1");

        assertEquals(200, result.getHttpCode());
        assertEquals("TEST", result.getData().getName());
    }

    @Test
    void testGetPermissionById_NotFound() {
        when(permissionRepository.findById(1)).thenReturn(Optional.empty());

        APIResultSet<PermissionDTO> result = permissionService.getPermissionById("1");

        assertEquals(404, result.getHttpCode());
    }

    @Test
    void testDeletePermission_Success() {
        when(permissionRepository.existsById(1)).thenReturn(true);
        doNothing().when(permissionRepository).deleteById(1);

        APIResultSet<Boolean> result = permissionService.deletePermission("1");

        assertEquals(200, result.getHttpCode());
        assertTrue(result.getData());
    }

    @Test
    void testDeletePermission_NotFound() {
        when(permissionRepository.existsById(1)).thenReturn(false);

        APIResultSet<Boolean> result = permissionService.deletePermission("1");

        assertEquals(404, result.getHttpCode());
    }

    @Test
    void testGetAllPermissions() {
        Permission p1 = new Permission();
        p1.setId(1);
        p1.setName("A");

        Permission p2 = new Permission();
        p2.setId(2);
        p2.setName("B");

        when(permissionRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        APIResultSet<List<PermissionDTO>> result = permissionService.getAllPermissions();

        assertEquals(200, result.getHttpCode());
        assertEquals(2, result.getData().size());
    }
}
