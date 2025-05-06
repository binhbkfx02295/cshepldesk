package com.binhbkfx02295.cshelpdesk.security.auth;

import com.binhbkfx02295.cshelpdesk.employee_management.authentication.service.AuthenticationServiceImpl;
import com.binhbkfx02295.cshelpdesk.security.exception.AuthenticationAPIException;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Locale;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFailureHandlerImpl implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException {
        log.info("in side login failure handler");
        // Gửi lỗi về view qua session
        HttpSession session = request.getSession();
        session.setAttribute("loginError", exception.getMessage());

        // Redirect về trang login để Thymeleaf render
        response.sendRedirect("/login");
    }
}
