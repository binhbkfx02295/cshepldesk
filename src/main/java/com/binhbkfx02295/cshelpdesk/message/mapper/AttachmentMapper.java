package com.binhbkfx02295.cshelpdesk.message.mapper;

import com.binhbkfx02295.cshelpdesk.message.dto.AttachmentDTO;
import com.binhbkfx02295.cshelpdesk.message.entity.Attachment;
import org.springframework.stereotype.Component;

@Component
public class AttachmentMapper {

    public AttachmentDTO toDTO(Attachment entity) {
        AttachmentDTO dto = new AttachmentDTO();
        dto.setId(entity.getId());
        dto.setType(entity.getType());
        dto.setUrl(entity.getUrl());
        dto.setStickerId(dto.getStickerId());
        return dto;
    }

    public Attachment toEntity(AttachmentDTO dto) {
        Attachment entity = new Attachment();
        entity.setType(dto.getType());
        entity.setUrl(dto.getUrl());
        entity.setStickerId(dto.getStickerId());
        return entity;
    }
}
