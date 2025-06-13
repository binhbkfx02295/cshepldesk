
package com.binhbkfx02295.cshelpdesk.openai.adapter;

import com.binhbkfx02295.cshelpdesk.openai.common.PromptBuilder;
import com.binhbkfx02295.cshelpdesk.openai.dto.OpenAIResponse;
import com.binhbkfx02295.cshelpdesk.openai.model.TicketEvaluateResult;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.dto.PerformanceSummaryDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.model.TicketAssessment;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketReportDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
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
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
public abstract class BaseGPTModelAdapter implements GPTModelAdapter {
    protected final RestTemplate restTemplate;
    protected final ObjectMapper objectMapper;
    protected final MasterDataCache masterDataCache;
    private final PromptBuilder promptBuilder;

    public BaseGPTModelAdapter(RestTemplate restTemplate, ObjectMapper objectMapper, MasterDataCache masterDataCache, PromptBuilder promptBuilder) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.masterDataCache = masterDataCache;
        this.promptBuilder = promptBuilder;
    }

    public GPTResult analyze(List<Message> messages) {
        ModelSettings config = getModelSettings();
        log.info(config.toString());
        String prompt = buildPrompt(messages);
        log.info(prompt);
        OpenAIResponse openAIResponse = getOpenAIResponse(config, prompt);
        GPTResult result = extractGPTResult(openAIResponse, config);
        log.info(result.toString());
        return result;
    }

    private OpenAIResponse getOpenAIResponse(ModelSettings config, String prompt) {
        String requestJson = buildRequestJson(config, prompt);
        String rawResponse = callOpenAI(config, requestJson);
        return parseOpenAIResponse(rawResponse);
    }

    public TicketEvaluateResult evaluateTicketByBatch(Map<String, Object> object) {
        String prompt = promptBuilder.buildBatchEvaluateTicket(object);
        TicketEvaluateResult result = new TicketEvaluateResult();
        log.info(prompt);
        System.out.println(prompt);
        //TODO: build requestJson
        String requestJson = buildRequestJson(getModelSettings(), prompt);
        log.info("request json {}", requestJson);
        //TODO: callOpenAI
        String rawResponse = callOpenAI(getModelSettings(), requestJson);

        log.info("rawResponse {}", rawResponse);
        OpenAIResponse openAIResponse = parseOpenAIResponse(rawResponse);
        log.info("ket qua {}", openAIResponse.toString());
        log.info("ket qua string {}", openAIResponse.getChoices().get(0).getMessage().getContent());
        try {
            List<TicketEvaluateResult.EvaluatedTicket> parseResult = objectMapper.readValue(openAIResponse.getChoices().get(0).getMessage().getContent(), new TypeReference<List<TicketEvaluateResult.EvaluatedTicket>>() {});
            result.setResult(parseResult);
        } catch (Exception e) {
            log.info("Loi parse json tu ket qua chatGPT", e);
        }

        return result;
    }

    protected abstract ModelSettings getModelSettings();

    protected String buildPrompt(List<Message> messages) {
        StringBuilder sb = new StringBuilder();

        sb.append("Bạn là chuyên gia đánh giá CSKH, hãy phân tích hội thoại giữa Nhân viên(1) và Khách hàng(0) và trích xuất:\n");

        List<Category> categories = masterDataCache.getAllCategories().values().stream().toList();
        sb.append("- Category (id:name): (");
        sb.append(categories.stream().map(c -> c.getId() + ":" + c.getName()).collect(Collectors.joining(",")));
        sb.append(")\n");

        List<Emotion> emotions = masterDataCache.getAllEmotions().values().stream().toList();
        sb.append("- Emotion (id:name): (");
        sb.append(emotions.stream().map(e -> e.getId() + ":" + e.getName()).collect(Collectors.joining(",")));
        sb.append(")\n");

        List<Satisfaction> satisfactions = masterDataCache.getAllSatisfactions().values().stream().toList();
        sb.append("- Satisfaction (id:name): (");
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
        sb.append("Yêu cầu: ");
        sb.append("- Xác định chính xác theo vào các dữ kiện được cho, nếu không xác định được, chọn Trung Lập hoặc Khác. \n");
        sb.append("- Nếu đọc xong nội dung trò chuyện mà không xác định được nội dung tổng quan thì không được giả định hay đoán mò nội dung cuộc trò chuyện, chỉ cần chọn chọn Trung Lập hoặc Khác.\n");
        sb.append("- Chỉ trả về 1 dòng JSON: {\"categoryId\":<id>,\"emotionId\":<id>,\"satisfactionId\":<id>}]\n");
        return sb.toString();
    }

    protected String buildRequestJson(ModelSettings config, String prompt) {
        try {
            var body = java.util.Map.of(
                    "model", config.getModelName(),
                    "messages", List.of(java.util.Map.of("role", "user", "content", prompt)),
                    "temperature", 0,
                    "max_tokens", 20000
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


    public String analyseStaff(PerformanceSummaryDTO report) throws RuntimeException {
        String prompt = """
                %s
               
                Bên trên là một json object chứa summary về performance của một nhân viên, bao gồm:
                 - tên nhân viên
                 - tháng đánh giá
                 - tổng hợp:
                   + phản hồi đầu: Tham chiếu là dưới 10s
                   + phản hồi trung bình: tham chiếu là dưới 30s
                   + xử lý trung bình: không có tham chiếu
                   + tổng tickets
                   + số tickets lỗi
                   + tỷ lệ lỗi: tham chiếu là dưới 20 %%
                   + danh sách lỗi gồm: tên, mô tả và số lượng lỗi, đã sort theo số lượng cao nhất
                   bạn hãy đưa ra kết luận ngắn gọn, súc tích, về Nhân viên này. bao gồm điểm mạnh nên tiếp tục phát huy, điểm cần cải thiện. Thêm tag html bootstrap để dễ đọc.
                   Ví dụ:
                    - Điểm mạnh:
                       + Thời gian phản hồi đầu rất nhanh
                       + ...
                    - Điểm cần cải thiện:
                       + Tỷ lệ lỗi 100%% là điều đáng báo động, cần nghiêm túc cải thiện
                       + Thời gian phản hồi trung bình rất lâu, cần nghiêm túc cải thiện
                    - Kết luận: Nhân viên Bình rất nhanh chóng trong đón tiếp khách hàng, tuy nhiên thường xuyên để khách hàng đợi phản hồi lâu là điều cần cải thiện. Đặc biệt: cần nghiêm túc cải thiện các lỗi hay gặp, nếu không khách hàng có khả năng đánh giá chất lượng dịch vụ rất tệ
                """.formatted(report);

        OpenAIResponse jsonResult = getOpenAIResponse(getModelSettings(), prompt);
        return jsonResult.getChoices().get(0).getMessage().getContent();
    };

}
