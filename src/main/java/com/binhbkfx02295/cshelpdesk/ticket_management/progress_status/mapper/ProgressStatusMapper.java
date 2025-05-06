package com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.mapper;

import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.entity.ProgressStatus;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.dto.ProgressStatusDTO;

public class ProgressStatusMapper {

    public ProgressStatusDTO toDTO(ProgressStatus progressStatus) {
        ProgressStatusDTO dto = new ProgressStatusDTO();
        dto.setCode(progressStatus.getCode());
        dto.setId(progressStatus.getId());
        dto.setName(progressStatus.getName());
        return dto;
    };

    public ProgressStatus toEntity(ProgressStatusDTO dto) {
        ProgressStatus entity = new ProgressStatus();
        entity.setCode(dto.getCode());
        entity.setName(dto.getCode());
        entity.setId(dto.getId());
        return entity;
    };
}
