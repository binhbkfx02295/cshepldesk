package com.binhbkfx02295.cshelpdesk.facebookuser.common;

import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserExportDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Slf4j
public class FacebookUserExporter {

    public static ByteArrayInputStream exportToExcel(List<FacebookUserExportDTO> users) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Khách hàng");

            // Header
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Facebook ID", "Tên facebook", "Họ tên", "Số điện thoại", "Email", "Zalo", "Ngày tham gia"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Data
            for (int i = 0; i < users.size(); i++) {
                FacebookUserExportDTO user = users.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(user.getFacebookId());
                row.createCell(1).setCellValue(user.getFacebookName());
                row.createCell(2).setCellValue(user.getRealName() != null ? user.getRealName(): "");
                row.createCell(3).setCellValue(user.getPhone() != null ? user.getPhone() : "");
                row.createCell(4).setCellValue(user.getEmail() != null ? user.getEmail() : "");
                row.createCell(5).setCellValue(user.getZalo() != null ? user.getZalo() : "");
                row.createCell(6).setCellValue(user.getCreatedAt().toString());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            log.info("Loi khong the tao excel", e);
            return null;
        }

    }
}
