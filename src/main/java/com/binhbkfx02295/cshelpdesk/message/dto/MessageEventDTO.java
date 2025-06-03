package com.binhbkfx02295.cshelpdesk.message.dto;

import com.binhbkfx02295.cshelpdesk.message.entity.Attachment;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDashboardDTO;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
public class MessageEventDTO {
    public int id;
    public TicketDashboardDTO ticket;
    public String text;
    public boolean senderEmployee;
    public Timestamp timestamp;
    public List<AttachmentDTO> attachments = new ArrayList<>();
}
