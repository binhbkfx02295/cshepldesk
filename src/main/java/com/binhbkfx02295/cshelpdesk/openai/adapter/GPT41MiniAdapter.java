package com.binhbkfx02295.cshelpdesk.openai.adapter;

import com.binhbkfx02295.cshelpdesk.infrastructure.common.cache.MasterDataCache;
import com.binhbkfx02295.cshelpdesk.openai.common.PromptBuilder;
import com.binhbkfx02295.cshelpdesk.openai.config.ModelRegistryConfig;
import com.binhbkfx02295.cshelpdesk.openai.model.ModelSettings;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Getter
@Setter
public class GPT41MiniAdapter extends BaseGPTModelAdapter{
    private final ModelSettings modelSettings;

    @Autowired
    public GPT41MiniAdapter(ModelRegistryConfig modelRegistryConfig,
                            RestTemplate restTemplate,
                            ObjectMapper objectMapper,
                            MasterDataCache masterDataCache,
                            PromptBuilder promptBuilder) {
        super(restTemplate, objectMapper, masterDataCache, promptBuilder);
        modelSettings = modelRegistryConfig.getGpt41Mini();
    }

    @Override
    public ModelSettings getModelSettings() {
        return modelSettings;
    }
}
