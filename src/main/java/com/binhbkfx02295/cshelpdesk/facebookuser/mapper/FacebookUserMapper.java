package com.binhbkfx02295.cshelpdesk.facebookuser.mapper;

import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.entity.FacebookUser;

public interface FacebookUserMapper {
    FacebookUserDTO toDTO(FacebookUser facebookUser);
}
