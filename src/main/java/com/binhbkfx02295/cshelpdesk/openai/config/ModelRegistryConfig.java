package com.binhbkfx02295.cshelpdesk.openai.config;

import com.binhbkfx02295.cshelpdesk.openai.model.ModelSettings;
import lombok.Data;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "openai.models")
@Data
public class ModelRegistryConfig {
    private ModelSettings gpt41Nano;
    private ModelSettings gpt41Mini;
    private ModelSettings gpt4oMini;

}
