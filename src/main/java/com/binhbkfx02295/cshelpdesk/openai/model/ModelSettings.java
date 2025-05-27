package com.binhbkfx02295.cshelpdesk.openai.model;

import lombok.Data;

@Data
public class ModelSettings {
    private String apiKey;
    private String modelName;
    private float ratePer1KTokens;
}
