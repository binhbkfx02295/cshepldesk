package com.binhbkfx02295.cshelpdesk.employee_management.employee.service;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.EmployeeDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.StatusDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.StatusLogDTO;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;

import java.util.List;

public interface EmployeeService {
    APIResultSet<EmployeeDTO> createUser(EmployeeDTO employeeDTO);
    APIResultSet<EmployeeDTO> updateUser(String username, EmployeeDTO employee);
    APIResultSet<Void> lockUser(String username);
    APIResultSet<EmployeeDTO> getUserByUsername(String username);
    APIResultSet<List<EmployeeDTO>> getAllUsers();
    APIResultSet<List<EmployeeDTO>> getUsersByGroup(int groupId);
    APIResultSet<Void> changePassword(String username, String newPassword);
    APIResultSet<Void> updateStatus(String username, boolean isActive);
    APIResultSet<Void> existsByUsername(String username);
    APIResultSet<List<EmployeeDTO>> findALlWithStatusLog();
    APIResultSet<StatusLogDTO> findFirstStatusLogByUsername(String username);
    APIResultSet<List<StatusLogDTO>> findWithAllLogs(EmployeeDTO employeeDTO);
    APIResultSet<Void> updateOnlineStatus(String username, StatusLogDTO logDTO);
    APIResultSet<List<StatusDTO>> getAllOnlineStatus();
    APIResultSet<Void> deleteByUsername(String testaccount);
}
