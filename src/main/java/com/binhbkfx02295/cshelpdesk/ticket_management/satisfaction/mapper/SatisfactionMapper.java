package com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.mapper;

import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.entity.Satisfaction;
import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.dto.SatisfactionDTO;

public class SatisfactionMapper {

    public SatisfactionDTO toDTO(Satisfaction satisfaction) {
        SatisfactionDTO dto = new SatisfactionDTO();
        dto.setId(satisfaction.getId());
        dto.setComment(satisfaction.getComment());
        dto.setScore(satisfaction.getScore());
        return dto;
    };

    public Satisfaction toEntity(SatisfactionDTO dto) {
        Satisfaction entity = new Satisfaction();
        entity.setId(dto.getId());
        entity.setComment(dto.getComment());
        entity.setScore(dto.getScore());
        return entity;
    }
}
