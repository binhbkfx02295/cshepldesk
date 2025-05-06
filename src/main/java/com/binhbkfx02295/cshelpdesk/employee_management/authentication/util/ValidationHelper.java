package com.binhbkfx02295.cshelpdesk.employee_management.authentication.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class ValidationHelper {

    private final MessageSource messageSource;

    public ValidationResult validateLoginInput(String username, String password, Locale locale) {
        ValidationResult result = new ValidationResult();

        if (username == null || !username.matches(ValidationConstants.USERNAME_PATTERN)) {
            String msg = messageSource.getMessage("validation.username.invalid", null, locale);
            result.addError("username", msg);
        }

        if (password == null || !password.matches(ValidationConstants.PASSWORD_PATTERN)) {
            String msg = messageSource.getMessage("validation.password.invalid", null, locale);
            result.addError("password", msg);
        }

        return result;
    }
}