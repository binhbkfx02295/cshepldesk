package com.binhbkfx02295.cshelpdesk.security.config;

import com.binhbkfx02295.cshelpdesk.security.auth.AuthenticationFailureHandlerImpl;
import com.binhbkfx02295.cshelpdesk.security.auth.AuthenticationSuccessHandlerImpl;
import com.binhbkfx02295.cshelpdesk.security.auth.CustomAuthenticationProvider;
import com.binhbkfx02295.cshelpdesk.security.auth.LogoutSuccessHandlerImpl;
import com.binhbkfx02295.cshelpdesk.security.filter.AlreadyAuthenticatedFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationProvider authenticationProvider;
    private final AuthenticationFailureHandlerImpl failureHandler;
    private final AuthenticationSuccessHandlerImpl successHandler;
    private final LogoutSuccessHandlerImpl logoutHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/login", "/css/**", "/js/**", "/img/**", "/webhook/**", "/api/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/process-login")          // xử lý login thành công
                        .defaultSuccessUrl("/dashboard")
                        .failureHandler(failureHandler)
                        .successHandler(successHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .logoutSuccessHandler(logoutHandler)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .invalidSessionUrl("/login?timeout=true")
                        .maximumSessions(1) // chỉ 1 session
                        .maxSessionsPreventsLogin(false)
                        .expiredUrl("/login?expired=true")
                )
                .addFilterBefore(new AlreadyAuthenticatedFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(authenticationProvider)
                .build(); // <== gọi ProviderManager constructor tại đây
    }
}
