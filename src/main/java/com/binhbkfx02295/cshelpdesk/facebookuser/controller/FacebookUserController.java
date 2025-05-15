package com.binhbkfx02295.cshelpdesk.facebookuser.controller;

import com.binhbkfx02295.cshelpdesk.facebookuser.common.FacebookUserExporter;
import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserExportDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserListDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserDetailDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserSearchCriteria;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketListDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketSearchCriteria;
import com.binhbkfx02295.cshelpdesk.util.APIResponseEntityHelper;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.facebookuser.service.FacebookUserServiceImpl;
import com.binhbkfx02295.cshelpdesk.util.PaginationResponse;
import com.binhbkfx02295.cshelpdesk.util.TicketExcelExporter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/facebookuser")
@Slf4j
public class FacebookUserController {

    FacebookUserServiceImpl facebookUserService;

    @Autowired
    public FacebookUserController(FacebookUserServiceImpl service) {
        facebookUserService = service;
    }

    @GetMapping("/ping")
    public String ping() {
        return "it works";
    }

    //get all
    @GetMapping(params = "!id")
    public ResponseEntity<APIResultSet<List<FacebookUserListDTO>>> getAll() {
        APIResultSet<List<FacebookUserListDTO>> resultSet = facebookUserService.getAll();
        return ResponseEntity.status(resultSet.getHttpCode()).body(resultSet);
    }

    @GetMapping(params = "id")
    public ResponseEntity<APIResultSet<FacebookUserDetailDTO>> get(@RequestParam String id) {
        APIResultSet<FacebookUserDetailDTO> resultSet = facebookUserService.get(id);
        return ResponseEntity.status(resultSet.getHttpCode()).body(resultSet);
    }

    @PostMapping
    public ResponseEntity<APIResultSet<FacebookUserDetailDTO>> createFacebookUser(@RequestBody FacebookUserDetailDTO user) {
        log.info(user.toString());
        APIResultSet<FacebookUserDetailDTO> resultSet = facebookUserService.save(user);
        return ResponseEntity.status(resultSet.getHttpCode()).body(resultSet);
    }


    @PutMapping()
    public ResponseEntity<APIResultSet<FacebookUserDetailDTO>> updateFacebookUser(@RequestBody FacebookUserDetailDTO user) {
        log.info(user.toString());
        return APIResponseEntityHelper.from(facebookUserService.update(user));
    }

    @GetMapping("/search")
    public ResponseEntity<APIResultSet<PaginationResponse<FacebookUserDetailDTO>>> searchFacebookUser(
            @ModelAttribute FacebookUserSearchCriteria criteria,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info(criteria.toString());
        log.info(pageable.toString());
        APIResultSet<PaginationResponse<FacebookUserDetailDTO>> resultSet = facebookUserService.searchUsers(criteria, pageable);
        return APIResponseEntityHelper.from(resultSet);
    }

    @DeleteMapping
    public ResponseEntity<APIResultSet<Void>> deleteFacebookUser(@RequestParam String id) {
        return APIResponseEntityHelper.from(facebookUserService.deleteById(id));
    }

    @GetMapping("/export-excel")
    public ResponseEntity<InputStreamResource> exportExcel(
            @ModelAttribute FacebookUserSearchCriteria criteria) {
        // Lấy tất cả dữ liệu, không phân trang
        log.info(criteria.toString());
        List<FacebookUserExportDTO> result = facebookUserService.exportSearchUsers(criteria, Pageable.unpaged());
        if (result == null) {
            return ResponseEntity.internalServerError().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Khach Hang.xlsx");
        ByteArrayInputStream in = FacebookUserExporter.exportToExcel(result);
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }


}
