package com.binhbkfx02295.cshelpdesk.webhook.dto;

import lombok.Data;
import java.util.List;

@Data
public class WebHookEventDTO {
    private String object;
    private List<Entry> entry;

    @Data
    public static class Entry {
        private String id;
        private long time;
        private List<Messaging> messaging;
    }

    @Data
    public static class Messaging {
        private Sender sender;
        private Recipient recipient;
        private long timestamp;
        private Message message;

        @Data
        public static class Sender {
            private String id;
        }

        @Data
        public static class Recipient {
            private String id;
        }

        @Data
        public static class Message {
            private String mid;
            private String text;
            private Boolean is_echo;
            private Long app_id;
        }
    }
}
