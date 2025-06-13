package com.binhbkfx02295.cshelpdesk.message.service;

import com.binhbkfx02295.cshelpdesk.infrastructure.common.cache.MasterDataCache;
import com.binhbkfx02295.cshelpdesk.message.dto.MessageDTO;
import com.binhbkfx02295.cshelpdesk.message.entity.Message;
import com.binhbkfx02295.cshelpdesk.message.mapper.MessageMapper;
import com.binhbkfx02295.cshelpdesk.message.repository.MessageRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.repository.TicketRepository;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.websocket.event.MessageEvent;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
    private final MasterDataCache cache;
    private final MessageMapper mapper;
    private final ApplicationEventPublisher publisher;
    private final EntityManager entityManager;

    @Override
    public APIResultSet<MessageDTO> addMessage(MessageDTO messageDTO) {
        try {
            Message saved = messageRepository.save(mapper.toEntity(messageDTO));
            entityManager.flush();
            entityManager.clear();
            cache.putMessage(messageRepository.findById(saved.getId()).get());
            cache.getTicket(messageDTO.getTicketId()).getMessages().add(saved);
            //TODO: publish event
            publisher.publishEvent(new MessageEvent(mapper.toEventDTO(cache.getMessage(saved.getId()))));
            return APIResultSet.ok("Message added successfully", mapper.toDTO(cache.getMessage(saved.getId())));
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
                    return message.getTicket().getId()==ticketId;
                }).toList();
            } else {
                messages = messageRepository.findByTicket_Id(ticketId);
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
