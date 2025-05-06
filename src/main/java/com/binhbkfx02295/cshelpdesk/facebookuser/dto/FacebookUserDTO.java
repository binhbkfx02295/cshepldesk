package com.binhbkfx02295.cshelpdesk.facebookuser.dto;

import com.binhbkfx02295.cshelpdesk.facebookuser.entity.FacebookUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FacebookUserDTO {
    private String facebookId;
    private String facebookFirstName;
    private String facebookLastName;
    private String facebookProfilePic;
    private String fullName;
    private String email;
    private String phone;
    private String zalo;

    public FacebookUserDTO(FacebookUser user) {
        facebookId = user.getFacebookId();
        facebookFirstName = user.getFacebookFirstName();
        facebookLastName = user.getFacebookLastName();
        facebookProfilePic = user.getFacebookProfilePic();
        fullName = user.getFullName();
        email = user.getEmail();
        phone = user.getPhone();
        zalo = user.getZalo();
    }
}
