package com.binhbkfx02295.cshelpdesk.infrastructure.security.auth;

import com.binhbkfx02295.cshelpdesk.employee_management.authentication.dto.LoginRequestDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.authentication.dto.LoginResponseDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.authentication.service.AuthenticationServiceImpl;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {


    private final AuthenticationServiceImpl authenticationService;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {


        String username = authentication.getName();
        String rawPassword = authentication.getCredentials().toString();
        log.info("⏺️ Bắt đầu xác thực: {}", username);
        log.info(rawPassword);
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setUsername(username);
        loginRequestDTO.setPassword(rawPassword);

        APIResultSet<LoginResponseDTO> result = authenticationService.login(loginRequestDTO);
        if (!result.isSuccess()) {
            log.warn("❌ Xác thực thất bại với mã HTTP {} - {}", result.getHttpCode(), result.getMessage());

            if (result.getHttpCode() == 400 || result.getHttpCode() == 401) {
                log.info("throw new BadCredentialsException(result.getMessage());");
                throw new BadCredentialsException(result.getMessage());
            } else if (result.getHttpCode() == 403) {
                log.info("throw new LockedException(result.getMessage());");
                throw new LockedException(result.getMessage());
            }
        }

        LoginResponseDTO response = result.getData();
        // Tạo danh sách quyền (authorities)
        Set<GrantedAuthority> authorities = response.getEmployeeDTO().getUserGroup().getPermissions().stream()
                .map(p -> new SimpleGrantedAuthority(p.getName()))
                .collect(Collectors.toSet());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + response.getEmployeeDTO().getUserGroup().getName().toUpperCase()));
        log.info("✅ Xác thực thành công: {}", username);
        log.info(authorities.toString());
        UserPrincipal principal = UserPrincipal.builder()
                .username(response.getEmployeeDTO().getUsername())
                .fullName(response.getEmployeeDTO().getName())
                .description(response.getEmployeeDTO().getDescription())
                .authorities(authorities)
                .build();

        return new UsernamePasswordAuthenticationToken(principal,null, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);

    }
}
