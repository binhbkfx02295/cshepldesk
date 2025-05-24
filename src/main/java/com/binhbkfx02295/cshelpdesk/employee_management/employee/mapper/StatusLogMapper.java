package com.binhbkfx02295.cshelpdesk.employee_management.employee.mapper;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.StatusLogDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Status;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.StatusLog;
import org.springframework.stereotype.Component;

@Component
public class StatusLogMapper {

    public StatusLogDTO toDTO(StatusLog entity) {
        StatusLogDTO statusLogDTO = new StatusLogDTO();
        statusLogDTO.setStatus(entity.getStatus().getName());
        statusLogDTO.setFrom(entity.getTimestamp());
        statusLogDTO.setUsername(entity.getEmployee().getUsername());
        return statusLogDTO;
    }

    public StatusLog toEntity(StatusLogDTO dto) {
        Employee employee = new Employee();
        Status status = new Status();
        status.setName(dto.getStatus());
        employee.setUsername(dto.getUsername());
        StatusLog entity = new StatusLog();
        entity.setStatus(status);
        entity.setTimestamp(dto.getFrom());
        entity.setEmployee(employee);
        return entity;
    }
}
