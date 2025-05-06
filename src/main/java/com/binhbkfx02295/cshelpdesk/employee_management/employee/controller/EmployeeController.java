package com.binhbkfx02295.cshelpdesk.employee_management.employee.controller;

import com.binhbkfx02295.cshelpdesk.employee_management.authentication.dto.LoginResponseDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.EmployeeDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.StatusDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.StatusLogDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.repository.StatusLogRepository;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.service.EmployeeServiceImpl;
import com.binhbkfx02295.cshelpdesk.employee_management.usergroup.UserGroupService;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.service.EmployeeService;
import com.binhbkfx02295.cshelpdesk.util.APIResponseEntityHelper;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee-management")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeServiceImpl employeeService;
    private final UserGroupService userGroupService;


    @PostMapping
    public ResponseEntity<APIResultSet<EmployeeDTO>> createUser(@RequestBody EmployeeDTO employeeDTO) {
        return APIResponseEntityHelper.from(employeeService.createUser(employeeDTO));
    }

    @GetMapping("/me")
    public ResponseEntity<APIResultSet<LoginResponseDTO>> getUserProfile(@AuthenticationPrincipal LoginResponseDTO loginDTO) {

        return APIResponseEntityHelper.from(APIResultSet.ok("ok", loginDTO));
    }

    @GetMapping("/{username}")
    public ResponseEntity<APIResultSet<EmployeeDTO>> getUserByUsername(@PathVariable String username) {
        return APIResponseEntityHelper.from(employeeService.getUserByUsername(username));
    }

    @GetMapping
    public ResponseEntity<APIResultSet<List<EmployeeDTO>>> getAllUsers() {
        return APIResponseEntityHelper.from(employeeService.getAllUsers());
    }

    @PutMapping("/{username}")
    public ResponseEntity<APIResultSet<EmployeeDTO>> updateUser(@PathVariable String username, @RequestBody EmployeeDTO employeeDTO) {
        return APIResponseEntityHelper.from(employeeService.updateUser(username, employeeDTO));
    }

    @PutMapping("/{username}/password")
    public ResponseEntity<APIResultSet<Void>> changePassword(@PathVariable String username, @RequestParam String newPassword) {
        return APIResponseEntityHelper.from(employeeService.changePassword(username, newPassword));
    }

    @PutMapping("/{username}/status")
    public ResponseEntity<APIResultSet<Void>> updateStatus(@PathVariable String username, @RequestParam boolean active) {
        return APIResponseEntityHelper.from(employeeService.updateStatus(username, active));
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<APIResultSet<List<EmployeeDTO>>> getUsersByGroup(@PathVariable int groupId) {
        return APIResponseEntityHelper.from(employeeService.getUsersByGroup(groupId));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<APIResultSet<List<EmployeeDTO>>> dashboard() {
        return APIResponseEntityHelper.from(employeeService.findALlWithStatusLog());
    }

    @GetMapping("/{username}/logs")
    public ResponseEntity<APIResultSet<List<StatusLogDTO>>> getLogs(@PathVariable String username) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setUsername(username);
        return APIResponseEntityHelper.from(employeeService.findWithAllLogs(dto));
    }

    @PostMapping("/{username}/log")
    public ResponseEntity<APIResultSet<Void>> updateStatusLog(@PathVariable String username, @RequestBody StatusLogDTO logDTO) {
        return APIResponseEntityHelper.from(employeeService.updateOnlineStatus(username, logDTO));
    }

    @GetMapping("/online-status")
    public ResponseEntity<APIResultSet<List<StatusDTO>>> getAllOnlineStatus() {
        return APIResponseEntityHelper.from(employeeService.getAllOnlineStatus());
    }

    @PostMapping("/{username}/online-status")
    public ResponseEntity<APIResultSet<List<StatusDTO>>> updateOnlineStatus(@PathVariable String username) {
        return APIResponseEntityHelper.from(employeeService.getAllOnlineStatus());
    }

}
