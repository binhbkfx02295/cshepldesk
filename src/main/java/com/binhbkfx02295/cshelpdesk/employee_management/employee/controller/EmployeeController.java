package com.binhbkfx02295.cshelpdesk.employee_management.employee.controller;

import com.binhbkfx02295.cshelpdesk.employee_management.authentication.dto.LoginResponseDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.*;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.service.EmployeeServiceImpl;
import com.binhbkfx02295.cshelpdesk.employee_management.usergroup.UserGroupService;
import com.binhbkfx02295.cshelpdesk.security.auth.UserPrincipal;
import com.binhbkfx02295.cshelpdesk.util.APIResponseEntityHelper;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee-management")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {

    private final EmployeeServiceImpl employeeService;
    private final UserGroupService userGroupService;


    @PostMapping
    public ResponseEntity<APIResultSet<EmployeeDTO>> createUser(@RequestBody EmployeeDTO employeeDTO) {
        return APIResponseEntityHelper.from(employeeService.createUser(employeeDTO));
    }

    @GetMapping("/")
    public ResponseEntity<APIResultSet<LoginResponseDTO>> getUserProfile(@AuthenticationPrincipal LoginResponseDTO loginDTO) {

        return APIResponseEntityHelper.from(APIResultSet.ok("ok", loginDTO));
    }

    @GetMapping("/get-user")
    public ResponseEntity<APIResultSet<EmployeeDTO>> getUserByUsername(@RequestParam(value = "user", required = false) String username) {
        return APIResponseEntityHelper.from(employeeService.getUserByUsername(username));
    }

    @GetMapping("/get-all-user")
    public ResponseEntity<APIResultSet<List<EmployeeDTO>>> getAllUsers() {
        return APIResponseEntityHelper.from(employeeService.getAllUsers());
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<APIResultSet<List<EmployeeDTO>>> getUsersByGroup(@PathVariable int groupId) {
        return APIResponseEntityHelper.from(employeeService.getUsersByGroup(groupId));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<APIResultSet<List<EmployeeDashboardDTO>>> dashboard() {
        return APIResponseEntityHelper.from(employeeService.findALlWithStatusLog());
    }

    //Employee profile
    @GetMapping("/me")
    public ResponseEntity<APIResultSet<UserPrincipal>> getUserProfile(
            @AuthenticationPrincipal UserPrincipal user) {
        return APIResponseEntityHelper.from(APIResultSet.ok("OK", user));
    }

    @PutMapping("/me")
    public ResponseEntity<APIResultSet<EmployeeDTO>> updateProfile(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody EmployeeDTO employeeDTO) {
        return APIResponseEntityHelper.from(employeeService.updateUser(user.getUsername(), employeeDTO));
    }

    @PutMapping
    public ResponseEntity<APIResultSet<EmployeeDTO>> updateUser(
            @RequestBody EmployeeDTO dto
    ) {
        return APIResponseEntityHelper.from(employeeService.updateUser(dto.getUsername(), dto));
    }

    @PutMapping("/me/password")
    public ResponseEntity<APIResultSet<Void>> changePassword(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody ChangePasswordDTO changePasswordDTO) {
        try {
            return APIResponseEntityHelper.from(employeeService.changePassword(
                    user.getUsername(),
                    changePasswordDTO.getPassword(),
                    changePasswordDTO.getNewPassword()));
        } catch (Exception e) {
            log.info(e.getMessage());
            return APIResponseEntityHelper.from(APIResultSet.internalError());
        }

    }

    @GetMapping("/me/online-status")
    public ResponseEntity<APIResultSet<StatusLogDTO>> getOnlineStatus(
            @AuthenticationPrincipal UserPrincipal user) {
        return APIResponseEntityHelper.from(employeeService.getLatestOnlineStatus(user.getUsername()));
    }

    @PutMapping("/me/online-status")
    public ResponseEntity<APIResultSet<Void>> updateOnlineStatus(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody StatusLogDTO logDTO ) {
        return APIResponseEntityHelper.from(employeeService.updateOnlineStatus(user.getUsername(), logDTO));
    }

}
