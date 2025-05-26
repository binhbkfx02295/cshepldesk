package com.binhbkfx02295.cshelpdesk.message.service;

import com.binhbkfx02295.cshelpdesk.infrastructure.common.cache.MasterDataCache;
import com.binhbkfx02295.cshelpdesk.message.dto.MessageDTO;
import com.binhbkfx02295.cshelpdesk.message.entity.Message;
import com.binhbkfx02295.cshelpdesk.message.mapper.MessageMapper;
import com.binhbkfx02295.cshelpdesk.message.repository.MessageRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.repository.TicketRepository;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final TicketRepository ticketRepository;
    private final MasterDataCache cache;
    private final MessageMapper mapper;

    @Override
    public APIResultSet<MessageDTO> addMessage(MessageDTO messageDTO) {
        try {
            Ticket ticket = ticketRepository.findById(messageDTO.getTicketId())
                    .orElse(null);
            if (ticket == null) {
                return APIResultSet.notFound("Ticket not found with ID: " + messageDTO.getTicketId());
            }

            Message message = new Message();
            message.setText(messageDTO.getText());
            message.setSenderEmployee(messageDTO.isSenderEmployee());
            message.setTimestamp(messageDTO.getTimestamp());
            message.setTicket(ticket);
            if (message.getTimestamp() == null) {
                message.setTimestamp(new Timestamp(System.currentTimeMillis()));
            }

            Message saved = messageRepository.save(message);
            cache.putMessage(saved);
            log.info("message saved: {}", messageDTO);
            return APIResultSet.ok("Message added successfully", mapper.toDTO(saved));
        } catch (Exception e) {
            return APIResultSet.internalError("Failed to save message: " + e.getMessage());
        }
    }

    @Override
    public APIResultSet<List<MessageDTO>> getMessagesByTicketId(int ticketId) {
        try {
            Ticket openingTicket = cache.getTicket(ticketId);
            List<Message> messages;

            if (openingTicket != null && cache.getAllMessages() != null) {
                messages = cache.getAllMessages().values().stream().filter(message -> {
                    log.info("loop ticketid {}, message.getTicket().getId() {}", ticketId, message.getTicket().getId());
                    return message.getTicket().getId()==ticketId;
                }).toList();
            } else {
                messages = messageRepository.findByTicket_Id(ticketId);
                log.info("hello, o day ne, retrieved msg list size {}", messages.size());
            }
            List<MessageDTO> dtos = messages.stream()
                    .map(mapper::toDTO)
                    .collect(Collectors.toList());
            return APIResultSet.ok("Messages retrieved successfully", dtos);
        } catch (Exception e) {
            return APIResultSet.internalError("Failed to retrieve messages: " + e.getMessage());
        }
    }

    public List<Message> toEntity(List<MessageDTO> dtos) {
        return dtos.stream().map(mapper::toEntity).toList();
    }
}
