package com.binhbkfx02295.cshelpdesk.ticket_management.emotion.mapper;

import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.entity.Emotion;
import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.dto.EmotionDTO;
import org.springframework.stereotype.Component;

@Component
public class EmotionMapper {
    public EmotionDTO toDTO(Emotion entity) {
        if (entity == null) return null;
        EmotionDTO dto = new EmotionDTO();
        dto.setCode(entity.getCode());
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;

    };
    public Emotion toEntity(EmotionDTO dto) {
        Emotion entity = new Emotion();
        entity.setId(dto.getId());
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        return entity;
    };
}
