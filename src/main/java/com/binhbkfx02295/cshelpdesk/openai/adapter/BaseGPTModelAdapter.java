
package com.binhbkfx02295.cshelpdesk.openai.adapter;

import com.binhbkfx02295.cshelpdesk.openai.dto.OpenAIResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.binhbkfx02295.cshelpdesk.message.entity.Message;
import com.binhbkfx02295.cshelpdesk.openai.config.ModelRegistryConfig;
import com.binhbkfx02295.cshelpdesk.openai.model.ModelSettings;
import com.binhbkfx02295.cshelpdesk.openai.model.GPTResult;
import com.binhbkfx02295.cshelpdesk.infrastructure.common.cache.MasterDataCache;
import com.binhbkfx02295.cshelpdesk.ticket_management.category.entity.Category;
import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.entity.Emotion;
import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.entity.Satisfaction;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
public abstract class BaseGPTModelAdapter implements GPTModelAdapter {
    protected final RestTemplate restTemplate;
    protected final ObjectMapper objectMapper;
    protected final MasterDataCache masterDataCache;

    public BaseGPTModelAdapter(RestTemplate restTemplate, ObjectMapper objectMapper, MasterDataCache masterDataCache) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.masterDataCache = masterDataCache;
    }

    public GPTResult analyze(List<Message> messages) {
        ModelSettings config = getModelSettings();
        log.info(config.toString());
        String prompt = buildPrompt(messages);
        log.info(prompt);
        String requestJson = buildRequestJson(config, prompt);
        String rawResponse = callOpenAI(config, requestJson);
        OpenAIResponse openAIResponse = parseOpenAIResponse(rawResponse);
        GPTResult result = extractGPTResult(openAIResponse, config);
        log.info(result.toString());
        return result;
    }

    protected abstract ModelSettings getModelSettings();

    protected String buildPrompt(List<Message> messages) {
        StringBuilder sb = new StringBuilder();

        sb.append("Bạn là chuyên gia đánh giá CSKH, hãy phân tích hội thoại giữa Nhân viên(1) và Khách hàng(0) và trích xuất:\n");

        List<Category> categories = masterDataCache.getAllCategories().values().stream().toList();
        sb.append("Category (id:name): (");
        sb.append(categories.stream().map(c -> c.getId() + ":" + c.getName()).collect(Collectors.joining(",")));
        sb.append(")\n");

        List<Emotion> emotions = masterDataCache.getAllEmotions().values().stream().toList();
        sb.append("Emotion (id:name): (");
        sb.append(emotions.stream().map(e -> e.getId() + ":" + e.getName()).collect(Collectors.joining(",")));
        sb.append(")\n");

        List<Satisfaction> satisfactions = masterDataCache.getAllSatisfactions().values().stream().toList();
        sb.append("Satisfaction (id:name): (");
        sb.append(satisfactions.stream().map(s -> s.getId() + ":" + s.getName()).collect(Collectors.joining(",")));
        sb.append(")\n");

        sb.append("Nội dung hội thoại như sau(phân cách bằng \\n\\n):\n\n");
        for (Message msg : messages) {
            sb.append(msg.isSenderEmployee() ? "1" : "0")
                    .append(": ")
                    .append(msg.getText().replace("\n", " "))
                    .append("\n");
        }
        sb.append("\n\n");
        sb.append("Yêu cầu: xác định chính xác theo nội dung được cho, nếu không xác định được, chọn Trung Lập hoặc Không xác định. Chỉ trả về 1 dòng JSON: {\"categoryId\":<id>,\"emotionId\":<id>,\"satisfactionId\":<id>}");
        return sb.toString();
    }

    protected String buildRequestJson(ModelSettings config, String prompt) {
        try {
            var body = java.util.Map.of(
                    "model", config.getModelName(),
                    "messages", List.of(java.util.Map.of("role", "user", "content", prompt))
            );
            return objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error building request JSON", e);
        }
    }

    protected String callOpenAI(ModelSettings config, String requestJson) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(config.getApiKey());
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.openai.com/v1/chat/completions", entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected OpenAIResponse parseOpenAIResponse(String json) {
        try {
            return objectMapper.readValue(json, OpenAIResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Cannot parse OpenAI response", e);
        }
    }

    protected GPTResult extractGPTResult(OpenAIResponse response, ModelSettings config) {
        String content = response.getChoices().get(0).getMessage().getContent();
        GPTResult result;
        try {
            result = objectMapper.readValue(content, GPTResult.class);
        } catch (Exception e) {
            throw new RuntimeException("Cannot parse GPT content json", e);
        }
        int tokens = response.getUsage().getTotal_tokens();
        float price = tokens * config.getRatePer1KTokens() / 1000.0f;
        result.setTokenUsed(tokens);
        result.setPrice(price);
        return result;
    }



}
