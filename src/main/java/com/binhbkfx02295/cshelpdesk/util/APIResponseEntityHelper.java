package com.binhbkfx02295.cshelpdesk.util;

import org.springframework.http.ResponseEntity;

public class APIResponseEntityHelper {

    private APIResponseEntityHelper() {
    }

    public static <T> ResponseEntity<APIResultSet<T>> from(APIResultSet<T> resultSet) {
        return ResponseEntity.status(resultSet.getHttpCode()).body(resultSet);
    }
}