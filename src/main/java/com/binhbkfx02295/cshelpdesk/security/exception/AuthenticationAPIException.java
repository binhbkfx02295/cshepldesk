package com.binhbkfx02295.cshelpdesk.security.exception;

import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import org.springframework.security.core.AuthenticationException;

public class AuthenticationAPIException extends AuthenticationException {
    private final APIResultSet<?> result;

    public AuthenticationAPIException(APIResultSet<?> result) {
        super(result.getMessage());
        this.result = result;
    }

    public APIResultSet<?> getResult() {
        return result;
    }
}
