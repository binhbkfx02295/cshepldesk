package com.binhbkfx02295.cshelpdesk.openai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GPTResult {
    private int emotionId;
    private int satisfactionId;
    private int categoryId;
    private int tokenUsed;
    private float price;
    private String gptModelused;
}
