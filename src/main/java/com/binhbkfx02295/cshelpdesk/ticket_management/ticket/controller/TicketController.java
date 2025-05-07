package com.binhbkfx02295.cshelpdesk.ticket_management.ticket.controller;

import com.binhbkfx02295.cshelpdesk.ticket_management.note.dto.NoteDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDetailDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketSearchCriteria;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.service.TicketServiceImpl;
import com.binhbkfx02295.cshelpdesk.util.APIResponseEntityHelper;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.util.PaginationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Set;

@RestController
@RequestMapping("/api/ticket")
@RequiredArgsConstructor
@Slf4j
public class TicketController {

    private final TicketServiceImpl ticketService;

    @GetMapping("/{id}")
    public ResponseEntity<APIResultSet<TicketDetailDTO>> getById(@PathVariable int id) {
        return APIResponseEntityHelper.from(ticketService.getTicketById(id));
    }

//    @GetMapping
//    public ResponseEntity<APIResultSet<PaginationResponse<TicketDTO>>> search(
//            @RequestParam(required = false) String username,
//            @RequestParam(required = false) Long facebookUserId,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
//            Pageable pageable
//    ) {
//        //TODO: search by criteria
//        return APIResponseEntityHelper.from(APIResultSet.notAllowed("not yet implemented"));
//    }


    @PostMapping
    public ResponseEntity<APIResultSet<TicketDetailDTO>> create(@RequestBody TicketDetailDTO dto) {
        log.info(dto.toString());
        return APIResponseEntityHelper.from(ticketService.createTicket(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResultSet<TicketDetailDTO>> update(@PathVariable int id, @RequestBody TicketDetailDTO dto) {
        return APIResponseEntityHelper.from(ticketService.updateTicket(id, dto));
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

    @PostMapping("/search")
    public ResponseEntity<APIResultSet<PaginationResponse<TicketDTO>>> search(
            @RequestBody TicketSearchCriteria criteria,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return APIResponseEntityHelper.from(ticketService.searchTickets(criteria, pageable));
    }


}