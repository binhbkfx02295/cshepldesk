package com.binhbkfx02295.cshelpdesk.employee_management.authentication.util;

public interface ValidationConstants {
    String USERNAME_PATTERN = "^[a-zA-Z0-9]{4,}$";

    // At least 8 chars, one uppercase, one lowercase, one special char from list
    String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+=,./;'\\[\\]<>:\"{}]).{8,}$";
}