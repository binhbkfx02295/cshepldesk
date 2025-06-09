package com.binhbkfx02295.cshelpdesk.message.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
public class MessageDTO {
    private int id;
    private Timestamp timestamp;
    private String text = "";
    private boolean senderEmployee;
    private int ticketId;
    private boolean senderSystem;
    private List<AttachmentDTO> attachments = new ArrayList<>();
}
