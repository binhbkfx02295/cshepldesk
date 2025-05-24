package com.binhbkfx02295.cshelpdesk.infrastructure.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class APIResultSet<D> {
    private static final int OK = HttpStatus.OK.value();
    private static final int FORBIDDEN = HttpStatus.FORBIDDEN.value();
    private static final int NOT_FOUND = HttpStatus.NOT_FOUND.value();
    private static final int BAD_REQUEST = HttpStatus.BAD_REQUEST.value();
    private static final int INTERNAL_ERROR = HttpStatus.INTERNAL_SERVER_ERROR.value();
    private static final int UNAUTHORIZED = HttpStatus.UNAUTHORIZED.value();

    private static final String MSG_OK = HttpStatus.OK.toString();
    private static final String MSG_NOT_FOUND = HttpStatus.NOT_FOUND.toString();
    private static final String MSG_FORBIDDEN = HttpStatus.FORBIDDEN.toString();
    private static final String MSG_BAD_REQUEST = HttpStatus.BAD_REQUEST.toString();
    private static final String MSG_INTERNAL_ERROR = HttpStatus.INTERNAL_SERVER_ERROR.toString();
    private static final String MSG_UNAUTHORIZED = HttpStatus.UNAUTHORIZED.toString();

    private int httpCode;
    private String message;
    private D data;

    public static <D> APIResultSet<D> ok() {
        return new APIResultSet<>(OK, MSG_OK, null);
    }

    public static <D> APIResultSet<D> ok(String msg, D data) {
        return new APIResultSet<>(OK,msg, data);
    }

    public static <D> APIResultSet<D> notAllowed(String message) {
        return new APIResultSet<>(OK, message, null);
    }

    public static <D> APIResultSet<D> notFound() {
        return new APIResultSet<>(NOT_FOUND, MSG_NOT_FOUND, null);
    }

    public static <D> APIResultSet<D> notFound(String message) {
        return new APIResultSet<>(NOT_FOUND, message, null);
    }

    public static <D> APIResultSet<D> forbidden() {
        return new APIResultSet<>(FORBIDDEN, MSG_FORBIDDEN, null);
    }

    public static <D> APIResultSet<D> forbidden(String message) {
        return new APIResultSet<>(FORBIDDEN, message, null);
    }

    public static <D> APIResultSet<D> badRequest() {
        return new APIResultSet<>(BAD_REQUEST, MSG_BAD_REQUEST, null);
    }

    public static <D> APIResultSet<D> badRequest(String message) {
        return new APIResultSet<>(BAD_REQUEST, message, null);
    }
    public static <D> APIResultSet<D> badRequest(String message, D data) {
        return new APIResultSet<>(BAD_REQUEST, message, data);
    }

    public static <D> APIResultSet<D> internalError() {
        return new APIResultSet<>(INTERNAL_ERROR, MSG_INTERNAL_ERROR, null);
    }

    public static <D> APIResultSet<D> internalError(String message) {
        return new APIResultSet<>(INTERNAL_ERROR, message, null);
    }

    public static <D> APIResultSet<D> unauthorized() {
        return new APIResultSet<>(UNAUTHORIZED, MSG_UNAUTHORIZED, null);
    }

    public static <D> APIResultSet<D> unauthorized(String message) {
        return new APIResultSet<>(UNAUTHORIZED, message, null);
    }

    public boolean isSuccess() {
        return httpCode == 200;
    }
}
