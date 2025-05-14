package com.binhbkfx02295.cshelpdesk.facebookuser.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacebookUserFetchDTO {
    @JsonProperty("id")
    private String facebookId;

    @JsonProperty("first_name")
    private String facebookFirstName;

    @JsonProperty("last_name")
    private String facebookLastName;

    @JsonProperty("profile_pic")
    private String facebookProfilePic;
}
