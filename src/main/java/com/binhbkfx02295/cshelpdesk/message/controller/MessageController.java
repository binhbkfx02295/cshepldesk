package com.binhbkfx02295.cshelpdesk.message.controller;

import com.binhbkfx02295.cshelpdesk.message.dto.MessageDTO;
import com.binhbkfx02295.cshelpdesk.message.service.MessageService;
import com.binhbkfx02295.cshelpdesk.util.APIResponseEntityHelper;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<APIResultSet<List<MessageDTO>>> getMessagesByTicket(
            @RequestParam("ticketId") int ticketId
    ) {
        APIResultSet<List<MessageDTO>> result = messageService.getMessagesByTicketId(ticketId);
        return APIResponseEntityHelper.from(result);
    }

    @PostMapping
    public ResponseEntity<APIResultSet<MessageDTO>> addMessage(
            @RequestBody MessageDTO messageDTO
    ) {
        APIResultSet<MessageDTO> result = messageService.addMessage(messageDTO);
        return APIResponseEntityHelper.from(result);
    }
}
