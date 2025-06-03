package com.binhbkfx02295.cshelpdesk.infrastructure.security.auth;

import com.binhbkfx02295.cshelpdesk.employee_management.authentication.service.AuthenticationServiceImpl;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.EmployeeDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.StatusDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.StatusLogDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.service.EmployeeServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    private final AuthenticationServiceImpl authenticationService;
    private final EmployeeServiceImpl employeeService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        employeeDTO.setUsername(user.getUsername());

        authenticationService.logout(employeeDTO);
        log.info("Inside logout handler");
        response.sendRedirect("/login?logout");
    }
}
