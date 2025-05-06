package com.binhbkfx02295.cshelpdesk.employee_management.authentication.util;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ValidationResult {
    private final Map<String, String> fieldErrors = new HashMap<>();

    public void addError(String field, String message) {
        fieldErrors.put(field, message);
    }

    public boolean hasErrors() {
        return !fieldErrors.isEmpty();
    }

    public boolean hasError(String field) {
        return fieldErrors.containsKey(field);
    }
}