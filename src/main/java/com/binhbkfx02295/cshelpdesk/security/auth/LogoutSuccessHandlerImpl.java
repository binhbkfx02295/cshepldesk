package com.binhbkfx02295.cshelpdesk.security.auth;

import com.binhbkfx02295.cshelpdesk.employee_management.authentication.dto.LoginResponseDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.authentication.service.AuthenticationServiceImpl;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.EmployeeDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    private final AuthenticationServiceImpl authenticationService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        LoginResponseDTO res = (LoginResponseDTO) authentication.getPrincipal();
        employeeDTO.setUsername(res.getEmployee().getUsername());

        authenticationService.logout(employeeDTO);
        log.info("Inside logout handler");
        response.sendRedirect("/login?logout");
    }
}
