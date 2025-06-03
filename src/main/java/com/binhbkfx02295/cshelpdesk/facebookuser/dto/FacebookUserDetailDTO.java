package com.binhbkfx02295.cshelpdesk.facebookuser.dto;

import com.binhbkfx02295.cshelpdesk.message.entity.Attachment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacebookUserDetailDTO {
    private String facebookId;
    private String facebookName;
    private String facebookProfilePic;
    private String realName;
    private String email;
    private String phone;
    private String zalo;
    private Instant createdAt;
}
