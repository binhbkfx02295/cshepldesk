package com.binhbkfx02295.cshelpdesk.employee_management.employee.mapper;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.StatusLogDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.StatusLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StatusLogMapper {

    private final StatusMapper statusMapper;

    public StatusLogDTO toDTO(StatusLog entity) {
        StatusLogDTO statusLogDTO = new StatusLogDTO();
        statusLogDTO.setStatus(statusMapper.toDTO(entity.getStatus()));
        statusLogDTO.setFrom(entity.getTimestamp());
        return statusLogDTO;
    }

    public StatusLog toEntity(StatusLogDTO dto) {
        StatusLog entity = new StatusLog();
        entity.setStatus(statusMapper.toEntity(dto.getStatus()));
        entity.setTimestamp(dto.getFrom());
        return entity;
    }
}
