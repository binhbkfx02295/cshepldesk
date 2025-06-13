package com.binhbkfx02295.cshelpdesk.ticket_management.performance.mapper;

import com.binhbkfx02295.cshelpdesk.ticket_management.performance.dto.CriteriaDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.dto.CriteriaDetailDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.dto.CriteriaFailedDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.model.Criteria;
import org.springframework.stereotype.Component;

@Component
public class CriteriaMapper {

    public final CriteriaDTO toDTO(Criteria entity) {
        CriteriaDTO dto = new CriteriaDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        return dto;
    }

    public final Criteria toEntity(CriteriaDetailDTO dto) {
        Criteria entity = new Criteria();
        entity.setDescription(dto.getDescription());
        entity.setName(dto.getName());
        entity.setActive(dto.isActive());
        entity.setCode(dto.getCode());
        entity.setId(dto.getId());
        return entity;
    }

    public void mergeToEntity(Criteria entity, CriteriaDetailDTO dto) {
        if (entity == null || dto == null) return;
        entity.setDescription(dto.getDescription());
        entity.setName(dto.getName());
        entity.setActive(dto.isActive());
        entity.setCode(dto.getCode());
        entity.setId(dto.getId());
    }

    public CriteriaDetailDTO toDetailDTO(Criteria entity) {
        CriteriaDetailDTO dto = new CriteriaDetailDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setActive(entity.isActive());
        return dto;

    }

    public CriteriaFailedDTO toCriteriaFailedDTO(Criteria criteria) {
        CriteriaFailedDTO dto = new CriteriaFailedDTO();
        dto.setCode(criteria.getCode());
        return dto;
    }
}
