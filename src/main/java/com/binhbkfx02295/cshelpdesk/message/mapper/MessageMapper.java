package com.binhbkfx02295.cshelpdesk.message.mapper;

import com.binhbkfx02295.cshelpdesk.message.dto.MessageDTO;
import com.binhbkfx02295.cshelpdesk.message.entity.Message;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public Message toEntity(MessageDTO dto) {
        Message entity = new Message();
        entity.setId(dto.getId());
        entity.setTimestamp(dto.getTimestamp());

        Ticket ticket = new Ticket();
        ticket.setId(dto.getTicketId());
        entity.setTicket(ticket);
        entity.setText(dto.getText());
        entity.setSenderEmployee(dto.isSenderEmployee());
        return entity;
    }

    public MessageDTO toDTO(Message entity) {
        MessageDTO dto = new MessageDTO();
        dto.setId(entity.getId());
        dto.setTimestamp(entity.getTimestamp());
        dto.setTicketId(entity.getTicket().getId());
        dto.setText(entity.getText());
        dto.setSenderEmployee(entity.isSenderEmployee());
        return dto;
    }
}
