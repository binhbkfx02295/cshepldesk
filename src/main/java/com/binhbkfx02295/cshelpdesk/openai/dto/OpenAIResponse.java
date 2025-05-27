package com.binhbkfx02295.cshelpdesk.openai.dto;

import lombok.Data;
import java.util.List;

@Data
public class OpenAIResponse {
    private List<Choice> choices;
    private Usage usage;

    @Data
    public static class Choice {
        private Message message;
    }
    @Data
    public static class Message {
        private String content;
    }
    @Data
    public static class Usage {
        private int total_tokens;
    }
}