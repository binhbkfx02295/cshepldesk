package com.binhbkfx02295.cshelpdesk.ticket_management.ticket.controller;

import com.binhbkfx02295.cshelpdesk.infrastructure.security.auth.UserPrincipal;
import com.binhbkfx02295.cshelpdesk.ticket_management.note.dto.NoteDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketListDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDashboardDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDetailDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketSearchCriteria;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.service.TicketServiceImpl;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResponseEntityHelper;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.PaginationResponse;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.TicketExcelExporter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Set;
import java.util.List;
@RestController
@RequestMapping("/api/ticket")
@RequiredArgsConstructor
@Slf4j
public class TicketController {

    private final TicketServiceImpl ticketService;

    @GetMapping()
    public ResponseEntity<APIResultSet<TicketDetailDTO>> getById(@RequestParam(value = "id") int id) {
        return APIResponseEntityHelper.from(ticketService.getTicketById(id));
    }

    @PostMapping
    public ResponseEntity<APIResultSet<TicketDetailDTO>> create(@RequestBody TicketDetailDTO dto) {
        log.info(dto.toString());
        return APIResponseEntityHelper.from(ticketService.createTicket(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResultSet<TicketDetailDTO>> update(@PathVariable int id, @RequestBody TicketDetailDTO dto) {
        return APIResponseEntityHelper.from(ticketService.updateTicket(id, dto));
    }

    @GetMapping("/get-by-facebook-id")
    public ResponseEntity<APIResultSet<List<TicketListDTO>>> getByFacebookId(@RequestParam(value = "id") String id) {
        return APIResponseEntityHelper.from(ticketService.findAllByFacebookUserId(id));
    }

    @PutMapping("/{ticketId}/note")
    public ResponseEntity<APIResultSet<Void>> addNote(@PathVariable int ticketId, @RequestBody NoteDTO noteDto) {
        System.out.println("Path varialbe " + ticketId);
        return APIResponseEntityHelper.from(ticketService.addNoteToTicket(ticketId, noteDto));
    }

    @DeleteMapping("/{ticketId}/note/{noteId}")
    public ResponseEntity<APIResultSet<Void>> removeNote(@PathVariable int ticketId, @PathVariable int noteId) {
        return APIResponseEntityHelper.from(ticketService.deleteNoteFromTicket(ticketId, noteId));
    }

    @GetMapping("/{ticketId}/note")
    public ResponseEntity<APIResultSet<Set<NoteDTO>>> getAllNotes(@PathVariable int ticketId) {
        return APIResponseEntityHelper.from(ticketService.getNotes(ticketId));
    }

    @GetMapping(value = "/search")
    public ResponseEntity<APIResultSet<PaginationResponse<TicketListDTO>>> search(
            @ModelAttribute TicketSearchCriteria criteria,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info(pageable.toString());
        return APIResponseEntityHelper.from(ticketService.searchTickets(criteria, pageable));
    }

    @GetMapping(value = "/search-report")
    public ResponseEntity<APIResultSet<PaginationResponse<TicketListDTO>>> search(
            @ModelAttribute TicketSearchCriteria criteria) {
        log.info(criteria.toString());
        return APIResponseEntityHelper.from(ticketService.searchTickets(criteria, Pageable.unpaged()));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<APIResultSet<List<TicketDashboardDTO>>> dashboard(
            @AuthenticationPrincipal UserPrincipal userPrincipal
            ) {
        return APIResponseEntityHelper.from(ticketService.getForDashboard(userPrincipal.getUsername()));
    }

    @PostMapping("/export-excel")
    public ResponseEntity<InputStreamResource> exportExcel(@RequestBody TicketSearchCriteria criteria) {
        try {
            // Lấy tất cả dữ liệu, không phân trang
            APIResultSet<PaginationResponse<TicketListDTO>> result = ticketService.searchTickets(criteria, Pageable.unpaged());
            List<TicketListDTO> tickets = result.getData().getContent();

            ByteArrayInputStream in = TicketExcelExporter.exportToExcel(tickets);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=tickets.xlsx");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(new InputStreamResource(in));
        } catch (IOException e) {
            log.error("Lỗi xuất Excel: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }


}