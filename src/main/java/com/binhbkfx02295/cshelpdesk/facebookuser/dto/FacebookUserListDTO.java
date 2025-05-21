package com.binhbkfx02295.cshelpdesk.facebookuser.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class FacebookUserListDTO {
    private String facebookId;
    private String facebookName;
    private String facebookProfilePic;
}