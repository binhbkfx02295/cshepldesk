package com.binhbkfx02295.cshelpdesk.employee_management.authentication.controller;

import com.binhbkfx02295.cshelpdesk.employee_management.authentication.dto.LoginRequestDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.authentication.dto.LoginResponseDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.authentication.service.AuthenticationService;
import com.binhbkfx02295.cshelpdesk.util.APIResponseEntityHelper;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {


    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<APIResultSet<LoginResponseDTO>> login(@RequestBody LoginRequestDTO requestDTO) {
        APIResultSet<LoginResponseDTO> response = authenticationService.login(requestDTO);
        return APIResponseEntityHelper.from(response);
    }

}
