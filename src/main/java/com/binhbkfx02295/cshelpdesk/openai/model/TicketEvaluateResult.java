package com.binhbkfx02295.cshelpdesk.openai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketEvaluateResult {
    private List<EvaluatedTicket> result;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvaluatedTicket {
        private long id;
        private List<Integer> failedCriterias;
        private String summary;
    }
}
