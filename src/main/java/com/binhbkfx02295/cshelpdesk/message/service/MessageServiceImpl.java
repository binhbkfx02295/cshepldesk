package com.binhbkfx02295.cshelpdesk.message.service;

import com.binhbkfx02295.cshelpdesk.message.dto.MessageDTO;
import com.binhbkfx02295.cshelpdesk.message.entity.Message;
import com.binhbkfx02295.cshelpdesk.message.repository.MessageRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.repository.TicketRepository;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.websocket.config.WebSocketConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final TicketRepository ticketRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketConfig webSocketConfig;

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

            Message saved = messageRepository.save(message);
            messagingTemplate.convertAndSend(webSocketConfig.getDestination(), messageDTO);
            log.info("message saved: {}", messageDTO);
            return APIResultSet.ok("Message added successfully", convertToDTO(saved));
        } catch (Exception e) {
            return APIResultSet.internalError("Failed to save message: " + e.getMessage());
        }
    }

    @Override
    public APIResultSet<List<MessageDTO>> getMessagesByTicketId(int ticketId) {
        try {
            List<Message> messages = messageRepository.findByTicket_Id(ticketId);
            List<MessageDTO> dtos = messages.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return APIResultSet.ok("Messages retrieved successfully", dtos);
        } catch (Exception e) {
            return APIResultSet.internalError("Failed to retrieve messages: " + e.getMessage());
        }
    }

    private MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setText(message.getText());
        dto.setTimestamp(message.getTimestamp());
        dto.setSenderEmployee(message.isSenderEmployee());
        dto.setTicketId(message.getTicket().getId());
        return dto;
    }
}
