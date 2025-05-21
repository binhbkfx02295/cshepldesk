package com.binhbkfx02295.cshelpdesk.facebookuser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacebookUserExportDTO {
    private String facebookId;
    private String facebookName;
    private String realName;
    private String email;
    private String phone;
    private String zalo;
    private Instant createdAt;
}
