package com.binhbkfx02295.cshelpdesk.frontend.controller;

import com.binhbkfx02295.cshelpdesk.employee_management.authentication.dto.LoginRequestDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.authentication.dto.LoginResponseDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.authentication.service.AuthenticationServiceImpl;
import com.binhbkfx02295.cshelpdesk.util.APIResponseEntityHelper;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Locale;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final AuthenticationServiceImpl authenticationService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        return "dashboard"; // layout.html sẽ chèn fragment này
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Thymeleaf login.html
    }

    @GetMapping("/ticket")
    public String ticket(Model model) {
        model.addAttribute("pageTitle", "Trang Dashboard");
        model.addAttribute("body", "dashboard :: body"); // tên file + tên fragment
        return "dashboard"; // layout.html sẽ chèn fragment này
    }

    @GetMapping("/customer")
    public String customer(Model model) {
        model.addAttribute("pageTitle", "Trang Dashboard");
        model.addAttribute("body", "dashboard :: body"); // tên file + tên fragment
        return "layout"; // layout.html sẽ chèn fragment này
    }

    @GetMapping("/performance")
    public String performance(Model model) {
        model.addAttribute("pageTitle", "Trang Dashboard");
        model.addAttribute("body", "dashboard :: body"); // tên file + tên fragment
        return "layout"; // layout.html sẽ chèn fragment này
    }

    @GetMapping("/reporting")
    public String reporting(Model model) {
        model.addAttribute("pageTitle", "Trang Dashboard");
        model.addAttribute("body", "dashboard :: body"); // tên file + tên fragment
        return "layout"; // layout.html sẽ chèn fragment này
    }

    @GetMapping("/setting")
    public String setting(Model model) {
        model.addAttribute("pageTitle", "Trang Dashboard");
        model.addAttribute("body", "dashboard :: body"); // tên file + tên fragment
        return "layout"; // layout.html sẽ chèn fragment này
    }
}
