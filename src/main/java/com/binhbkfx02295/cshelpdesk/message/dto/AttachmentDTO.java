package com.binhbkfx02295.cshelpdesk.message.dto;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AttachmentDTO {
    private int id;
    private String type;
    private String url;
    private Long stickerId;
}
