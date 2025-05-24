package com.binhbkfx02295.cshelpdesk.frontend.controller;

import com.binhbkfx02295.cshelpdesk.employee_management.authentication.service.AuthenticationServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class MainController {

    @GetMapping("/")
    public void index(HttpServletResponse response) throws IOException {
        response.sendRedirect("/today-staff");
    }

    @GetMapping("/today-staff")
    public String todayStaff() {
        return "today-staff";
    }

    @GetMapping("/today-ticket")
    public String todayTicket() {
        return "today-ticket";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/ticket")
    public String ticket() {
        return "ticket";
    }

    @GetMapping("/customer")
    public String customer() {
        return "customer";
    }

    @GetMapping("/performance")
    public String performance() {
        return "performance";
    }

    @GetMapping("/report")
    public String report() {
        return "report"; // layout.html sẽ chèn fragment này
    }

    @GetMapping("/setting")
    public String setting() {
        return "setting"; // layout.html sẽ chèn fragment này
    }

    @GetMapping("/error")
    public String error(HttpServletResponse res, HttpServletRequest req) throws IOException {

        String path =req.getRequestURI();
        String accept = req.getHeader("Accept");

        // Nếu là file tĩnh (css/js/img/...) → không trả HTML
        if (isStaticAsset(path) || accept == null || !accept.contains("text/html")) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.setContentType("application/json");
            res.getWriter().write("{\"error\": \"Not found\"}");
            return null;
        }

        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return "error"; // Chỉ khi là HTML trang chính
    }

    @GetMapping("/pending")
    public String pending() {
        return "pending"; // layout.html sẽ chèn fragment này
    }

    private boolean isStaticAsset(String path) {
        return path.matches(".*\\.(css|js|png|jpg|jpeg|gif|svg|ico|woff2?|ttf|eot)$");
    }
}
