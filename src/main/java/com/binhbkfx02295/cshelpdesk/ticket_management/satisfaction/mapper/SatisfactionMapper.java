package com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.mapper;

import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.entity.Satisfaction;
import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.dto.SatisfactionDTO;
import org.springframework.stereotype.Component;

@Component
public class SatisfactionMapper {

    public SatisfactionDTO toDTO(Satisfaction satisfaction) {
        if (satisfaction == null) return null;
        SatisfactionDTO dto = new SatisfactionDTO();
        dto.setId(satisfaction.getId());
        dto.setCode(satisfaction.getCode());
        dto.setName(satisfaction.getName());
        return dto;
    };

    public Satisfaction toEntity(SatisfactionDTO dto) {
        Satisfaction entity = new Satisfaction();
        entity.setId(dto.getId());
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        return entity;
    }
}
