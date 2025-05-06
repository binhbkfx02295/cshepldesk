package com.binhbkfx02295.cshelpdesk.employee.service;

import com.binhbkfx02295.cshelpdesk.common.cache.MasterDataCache;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.EmployeeDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee;
import com.binhbkfx02295.cshelpdesk.employee_management.usergroup.UserGroup;
import com.binhbkfx02295.cshelpdesk.employee_management.permission.PermissionRepository;
import com.binhbkfx02295.cshelpdesk.employee_management.usergroup.UserGroupRepository;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.repository.EmployeeRepository;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.service.EmployeeServiceImpl;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private UserGroupRepository userGroupRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MasterDataCache cache;

    @InjectMocks
    private EmployeeServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private EmployeeDTO buildUserDTO() {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setUsername("testuser");
        dto.setName("Test User");
        dto.setPassword("password123");
        dto.setGroupId(1);
        dto.setDescription("Test description");
        return dto;
    }

    @Test
    void testCreateUserSuccess() {
        EmployeeDTO dto = buildUserDTO();
        UserGroup group = new UserGroup();
        group.setGroupId(1);

        when(employeeRepository.existsById(dto.getUsername())).thenReturn(false);
        when(userGroupRepository.findById(dto.getGroupId())).thenReturn(Optional.of(group));
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPass");

        APIResultSet<EmployeeDTO> result = userService.createUser(dto);

        assertEquals(200, result.getHttpCode());
        assertEquals("User created", result.getMessage());
        assertEquals(dto.getUsername(), result.getData().getUsername());

        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(captor.capture());
        assertEquals("encodedPass", captor.getValue().getPassword());
    }

    @Test
    void testCreateUserExistingUsername() {
        EmployeeDTO dto = buildUserDTO();

        when(employeeRepository.existsById(dto.getUsername())).thenReturn(true);

        APIResultSet<EmployeeDTO> result = userService.createUser(dto);

        assertEquals(400, result.getHttpCode());
        assertEquals("Username already exists", result.getMessage());
    }

    @Test
    void testUpdateUserSuccess() {
        EmployeeDTO dto = buildUserDTO();
        dto.setName("Updated Name");
        dto.setDescription("Updated Description");

        Employee user = new Employee();
        user.setUsername(dto.getUsername());
        user.setUserGroup(new UserGroup());

        UserGroup group = new UserGroup();
        group.setGroupId(dto.getGroupId());

        when(employeeRepository.findById(dto.getUsername())).thenReturn(Optional.of(user));
        when(userGroupRepository.findById(dto.getGroupId())).thenReturn(Optional.of(group));

        APIResultSet<EmployeeDTO> result = userService.updateUser(dto.getUsername(), dto);

        assertEquals(200, result.getHttpCode());
        assertEquals("User updated", result.getMessage());
        verify(employeeRepository).save(user);
    }

    @Test
    void testLockUserSuccess() {
        Employee user = new Employee();
        user.setUsername("testuser");
        user.setActive(true);

        when(employeeRepository.findById("testuser")).thenReturn(Optional.of(user));
        when(employeeRepository.save(user)).thenReturn(user);

        APIResultSet<Void> result = userService.lockUser("testuser");

        assertEquals(200, result.getHttpCode());
        assertFalse(user.isActive());
    }

    @Test
    void testChangePasswordSuccess() {
        Employee user = new Employee();
        user.setUsername("testuser");

        when(employeeRepository.findById("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpass")).thenReturn("encodedNew");

        APIResultSet<Void> result = userService.changePassword("testuser", "newpass");

        assertEquals(200, result.getHttpCode());
        assertEquals("encodedNew", user.getPassword());
    }

    @Test
    void testUpdateStatusSuccess() {
        Employee user = new Employee();
        user.setUsername("testuser");
        user.setActive(true);

        when(employeeRepository.findById("testuser")).thenReturn(Optional.of(user));

        APIResultSet<Void> result = userService.updateStatus("testuser", false);

        assertEquals(200, result.getHttpCode());
        assertFalse(user.isActive());
    }
}
