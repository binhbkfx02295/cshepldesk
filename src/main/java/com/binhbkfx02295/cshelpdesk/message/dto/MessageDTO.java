package com.binhbkfx02295.cshelpdesk.message.dto;

import com.binhbkfx02295.cshelpdesk.facebookuser.entity.FacebookUser;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class MessageDTO {
    private int id;
    private Timestamp timestamp;
    private String text;
    private boolean senderEmployee;
    private int ticketId;
}
