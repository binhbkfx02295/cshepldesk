package com.binhbkfx02295.cshelpdesk.ticket_management.report.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Report {
    private Dataset dataset;
    private Map<String, Object> summary;
    private TabularData tabularData;
    private String title;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Dataset {
        private List<String> labels;
        private String label;
        private String type;
        private boolean main;
        private List<? extends Number> data;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TabularData {
        private List<String> columns; // Danh sách tên cột
        private List<Object> rows; // Dữ liệu từng dòng (map columnName -> value)
    }
}
