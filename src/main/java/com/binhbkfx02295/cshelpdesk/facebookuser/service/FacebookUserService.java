package com.binhbkfx02295.cshelpdesk.facebookuser.service;


import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserDetailDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.entity.FacebookUser;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;

import java.util.List;

public interface FacebookUserService {

    static final String MSG_ERROR_USER_NOT_FOUND = "MSG_ERROR_USER_NOT_FOUND";

    static final String MSG_ERROR_SEARCH_NOT_FOUND = "MSG_ERROR_SEARCH_NOT_FOUND";

    static final String MSG_SUCCESS = "Success";

    static final String MSG_ERROR_BAD_REQUEST = "MSG_ERROR_BAD_REQUEST";

    static final String MSG_ERROR_INTERNAL_SERVER_ERROR = "MSG_ERROR_INTERNAL_SERVER_ERROR";

    static final String MSG_ERROR_NOT_ALLOWED = "MSG_ERROR_NOT_ALLOWED";

    APIResultSet<FacebookUserDTO> save(FacebookUserDTO user);

    APIResultSet<FacebookUserDetailDTO> update(FacebookUserDetailDTO updatedUser);

    APIResultSet<List<FacebookUserDTO>> search(String name);

    APIResultSet<FacebookUserDTO> get(String id);

    APIResultSet<List<FacebookUserDTO>> getAll();

    APIResultSet<Void> existsById(String facebookId);

    APIResultSet<Void> deleteById(String s);
}
