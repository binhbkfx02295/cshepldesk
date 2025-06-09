package com.binhbkfx02295.cshelpdesk.infrastructure.util;

import java.util.regex.Pattern;

public class PasswordValidator {
    public static final String VALIDATION_ERROR = "Mật khẩu phải từ 6 ký tự, gồm A-Z, a-z, 0-9 và ký tự đặc biệt như !@#$...";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^.*(?=.{6,})(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=\\[\\]{};':\"\\\\|,.<>/?-]).*$"
    );

    public static String validate(String password) {

        if (password == null || !PASSWORD_PATTERN.matcher(password.trim()).matches()) {
            return VALIDATION_ERROR;
        }
        return null;
    }
}
