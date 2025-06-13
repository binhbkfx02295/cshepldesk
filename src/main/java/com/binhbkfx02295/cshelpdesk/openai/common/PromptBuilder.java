package com.binhbkfx02295.cshelpdesk.openai.common;

import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketReportDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PromptBuilder {

    private final ObjectMapper objectMapper;

    public String buildBatchEvaluateTicket(Map<String, Object> object) {
        String prompt = "";
        try {
            // Chuyển object thành JSON string
            String jsonData = objectMapper.writeValueAsString(object);

            // Xây dựng prompt
            prompt = """
                    Bạn là chuyên gia đánh giá chất lượng phục vụ khách hàng dựa trên các tiêu chí sau và danh sách ticket kèm hội thoại:
                            
                    1. Các tiêu chí đánh giá (criterias) có cấu trúc JSON như sau:
                    %s
                            
                    2. Nhiệm vụ của bạn: Phân tích từng ticket, dựa trên nội dung hội thoại trong "messages" theo, xác định ticket vi phạm những tiêu chí nào.
                    Nếu nội dung quá ngắn, hoặc nội dung bất thường, hoặc không thể xác định nội dung cuộc trò chuyện, hãy chỉ trả về summary là "Không đủ dữ liệu để đánh giá".
                    Nếu không có lỗi, hãy chỉ để summary là "Chưa có lỗi".
                    Hãy cố gắng đánh giá chỉ trong phạm vi criterias cho phép, nếu bạn bối rối và không chắc câu đó có vi phạm không, hãy bỏ qua.
                            
                    3. Kết quả trả về: Một JSON array gồm các ticket với id, danh sách failedCriterias (id các tiêu chí vi phạm), summary là chỉ cụ thể câu vi phạm số mấy:
                    [
                      {"id": 5, "failedCriterias": [1, 5, 9], "summary": "..bằng chữ ngắn gọn.."},
                      {"id": 7, "failedCriterias": [2, 3], "summary": "..bằng chữ ngắn gọn..}
                    ]
                            
                    4. Dữ liệu đầu vào dưới đây:
                    %s
                            
                    Hãy chỉ trả về một object đúng chuẩn JSON chứa kết quả theo đúng định dạng ở mục 3, không thêm bất kỳ ký tự khác.
                    """.formatted(
                    objectMapper.writeValueAsString(object.get("criterias")),
                    objectMapper.writeValueAsString(object.get("tickets")));

        } catch (Exception e) {
            log.error("Lỗi khi build prompt đánh giá ticket", e);
        }
        return prompt;
    }
}
