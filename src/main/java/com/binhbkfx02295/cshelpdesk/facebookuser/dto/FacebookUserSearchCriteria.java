package com.binhbkfx02295.cshelpdesk.facebookuser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacebookUserSearchCriteria {
    private String facebookId;
    private String facebookName;
    private String realName;
    private String email;
    private String phone;
    private String zalo;
}
