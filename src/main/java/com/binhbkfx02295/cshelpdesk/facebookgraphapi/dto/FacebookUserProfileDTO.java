package com.binhbkfx02295.cshelpdesk.facebookgraphapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacebookUserProfileDTO {
    private String id;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private String email;
    private Picture picture;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Picture {
        private Data data;

        @lombok.Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Data {
            private String url;
        }
    }
}