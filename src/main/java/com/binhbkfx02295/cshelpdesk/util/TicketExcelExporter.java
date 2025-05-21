package com.binhbkfx02295.cshelpdesk.util;

import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDetailDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketListDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class TicketExcelExporter {

    public static ByteArrayInputStream exportToExcel(List<TicketListDTO> tickets) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Tickets");

            // Header
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Tiêu đề", "Người xử lý", "Trạng thái", "Ngày tạo"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Data
            for (int i = 0; i < tickets.size(); i++) {
                TicketListDTO ticket = tickets.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(ticket.getId());
                row.createCell(1).setCellValue(ticket.getTitle());
                row.createCell(2).setCellValue(ticket.getAssignee() != null ? ticket.getAssignee().getName() : "");
                row.createCell(3).setCellValue(ticket.getProgressStatus() != null ? ticket.getProgressStatus().getName() : "");
                row.createCell(4).setCellValue(ticket.getCreatedAt().toString());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
