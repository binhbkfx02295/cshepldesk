package com.binhbkfx02295.cshelpdesk.openai.adapter;


import com.binhbkfx02295.cshelpdesk.infrastructure.common.cache.MasterDataCache;
import com.binhbkfx02295.cshelpdesk.openai.config.ModelRegistryConfig;
import com.binhbkfx02295.cshelpdesk.openai.model.ModelSettings;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GPT41NanoAdapter extends BaseGPTModelAdapter {
    private final ModelSettings modelSettings;

    @Autowired
    public GPT41NanoAdapter(ModelRegistryConfig config,
                            RestTemplate restTemplate,
                            ObjectMapper objectMapper,
                            MasterDataCache masterDataCache, ModelRegistryConfig registry1) {
        super(restTemplate, objectMapper, masterDataCache);
        this.modelSettings = config.getGpt41Nano();
    }


    @Override
    protected ModelSettings getModelSettings() {
        return this.modelSettings;
    }
}
