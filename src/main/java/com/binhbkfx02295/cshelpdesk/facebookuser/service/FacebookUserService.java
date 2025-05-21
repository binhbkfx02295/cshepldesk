package com.binhbkfx02295.cshelpdesk.facebookuser.service;


import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserListDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserDetailDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserFetchDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserSearchCriteria;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.util.PaginationResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FacebookUserService {

    static final String MSG_ERROR_USER_NOT_FOUND = "MSG_ERROR_USER_NOT_FOUND";

    static final String MSG_ERROR_SEARCH_NOT_FOUND = "MSG_ERROR_SEARCH_NOT_FOUND";

    static final String MSG_SUCCESS = "Success";

    static final String MSG_ERROR_BAD_REQUEST = "MSG_ERROR_BAD_REQUEST";

    static final String MSG_ERROR_INTERNAL_SERVER_ERROR = "MSG_ERROR_INTERNAL_SERVER_ERROR";

    static final String MSG_ERROR_NOT_ALLOWED = "MSG_ERROR_NOT_ALLOWED";

    APIResultSet<FacebookUserDetailDTO> save(FacebookUserDetailDTO save);

    APIResultSet<FacebookUserDetailDTO> save(FacebookUserFetchDTO save);

    APIResultSet<FacebookUserDetailDTO> update(FacebookUserDetailDTO updatedUser);

    APIResultSet<FacebookUserDetailDTO> get(String id);

    APIResultSet<List<FacebookUserListDTO>> getAll();

    APIResultSet<Void> existsById(String facebookId);

    APIResultSet<Void> deleteById(String s);

    APIResultSet<PaginationResponse<FacebookUserDetailDTO>> searchUsers(FacebookUserSearchCriteria criteria, Pageable pageable);
}
