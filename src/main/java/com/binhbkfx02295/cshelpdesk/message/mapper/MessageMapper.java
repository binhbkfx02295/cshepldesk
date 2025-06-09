package com.binhbkfx02295.cshelpdesk.message.mapper;

import com.binhbkfx02295.cshelpdesk.message.dto.AttachmentDTO;
import com.binhbkfx02295.cshelpdesk.message.dto.MessageDTO;
import com.binhbkfx02295.cshelpdesk.message.dto.MessageEventDTO;
import com.binhbkfx02295.cshelpdesk.message.entity.Attachment;
import com.binhbkfx02295.cshelpdesk.message.entity.Message;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.mapper.TicketMapper;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageMapper {
    private final TicketMapper ticketMapper;
    private final TicketRepository repository;
    private final AttachmentMapper attachmentMapper;

    public Message toEntity(MessageDTO dto) {
        Message entity = new Message();
        Ticket ticket = repository.getReferenceById(dto.getTicketId());
        entity.setId(dto.getId());
        entity.setTimestamp(dto.getTimestamp());
        entity.setTicket(ticket);
        entity.setText(dto.getText() == null ? "" : dto.getText());
        entity.setSenderEmployee(dto.isSenderEmployee());
        entity.setSenderSystem(dto.isSenderSystem());
        if (dto.getAttachments() != null && !dto.getAttachments().isEmpty()) {
            for (AttachmentDTO attachmentDTO: dto.getAttachments()) {
                Attachment attachment = attachmentMapper.toEntity(attachmentDTO);
                attachment.setMessage(entity);
                entity.getAttachments().add(attachment);
            }
        }
        return entity;
    }

    public MessageDTO toDTO(Message entity) {
        MessageDTO dto = new MessageDTO();
        dto.setId(entity.getId());
        dto.setTimestamp(entity.getTimestamp());
        dto.setTicketId(entity.getTicket().getId());
        dto.setText(entity.getText());
        dto.setSenderEmployee(entity.isSenderEmployee());
        dto.setSenderSystem(entity.isSenderSystem());

        if (entity.getAttachments() != null && !entity.getAttachments().isEmpty()) {
            for (Attachment attachment: entity.getAttachments()) {
                dto.getAttachments().add(attachmentMapper.toDTO(attachment));
            }
        }

        return dto;
    }

    public MessageEventDTO toEventDTO(Message entity) {
        MessageEventDTO dto = new MessageEventDTO();
        dto.setId(entity.getId());
        dto.setTimestamp(entity.getTimestamp());
        dto.setTicket(ticketMapper.toDashboardDTO(entity.getTicket()));
        dto.setText(entity.getText());
        dto.setSenderEmployee(entity.isSenderEmployee());
        dto.setSenderSystem(entity.isSenderSystem());

        if (entity.getAttachments() != null && !entity.getAttachments().isEmpty()) {
            for (Attachment attachment: entity.getAttachments()) {
                dto.getAttachments().add(attachmentMapper.toDTO(attachment));
            }
        }
        return dto;
    }
}
