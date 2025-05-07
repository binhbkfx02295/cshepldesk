package com.binhbkfx02295.cshelpdesk.facebookuser.dto;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacebookUserDetailDTO {
    private String facebookId;
    private String facebookFirstName;
    private String facebookLastName;
    private String facebookProfilePic;
    private String fullName;
    private String email;
    private String phone;
    private String zalo;
}
