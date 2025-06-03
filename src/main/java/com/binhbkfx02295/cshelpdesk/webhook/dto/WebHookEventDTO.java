package com.binhbkfx02295.cshelpdesk.webhook.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebHookEventDTO {
    private String object;
    private List<Entry> entry;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Entry {
        private String id;
        private Long time;
        private List<Messaging> messaging;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Messaging {
        private User sender;
        private User recipient;
        private Long timestamp;
        private Message message;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class User {
            private String id;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Message {
            private String mid;
            private String text;
            private List<Attachment> attachments;
            @JsonProperty("sticker_id")
            private Long stickerId;
            @JsonProperty("quick_reply")
            private QuickReply quickReply;


        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Attachment {
        private String type; // image, audio, video, file, fallback, location
        private Payload payload;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Payload {
            private String url;
            // Có thể có thêm các trường như coordinates nếu là location, sticker_id nếu là sticker
            @JsonProperty("sticker_id")
            private Long stickerId;
            private Coordinates coordinates;

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Coordinates {
                private Double lat;
                private Double longt; // Tùy theo response Facebook là "long" hay "lng"
            }
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QuickReply {
        private String payload;
    }
}
