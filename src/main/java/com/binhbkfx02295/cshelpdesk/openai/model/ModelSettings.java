package com.binhbkfx02295.cshelpdesk.openai.model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class ModelSettings {
    @Value("${openai.models.gpt41nano.api-key}")
    private String apiKey;
    private String modelName;
    private float ratePer1KTokens;
}
