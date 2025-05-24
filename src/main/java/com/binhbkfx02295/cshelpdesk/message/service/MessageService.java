package com.binhbkfx02295.cshelpdesk.message.service;

import com.binhbkfx02295.cshelpdesk.message.dto.MessageDTO;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;

import java.util.List;

public interface MessageService {
    APIResultSet<MessageDTO> addMessage(MessageDTO messageDTO);
    APIResultSet<List<MessageDTO>> getMessagesByTicketId(int ticketId);

}
