package com.binhbkfx02295.cshelpdesk.employee_management.employee.mapper;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.StatusLogDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.StatusLog;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
@RequiredArgsConstructor
public class StatusLogMapper {

    private final StatusMapper statusMapper;
    private final EmployeeRepository employeeRepository;

    public StatusLogDTO toDTO(StatusLog entity) {
        StatusLogDTO statusLogDTO = new StatusLogDTO();
        statusLogDTO.setStatus(statusMapper.toDTO(entity.getStatus()));
        statusLogDTO.setFrom(entity.getTimestamp());
        statusLogDTO.setUsername(entity.getEmployee().getUsername());
        return statusLogDTO;
    }

    public StatusLog toEntity(StatusLogDTO dto) {
        StatusLog entity = new StatusLog();
        entity.setStatus(statusMapper.toEntity(dto.getStatus()));
        entity.setTimestamp(dto.getFrom());
        if (entity.getTimestamp() == null) {
            entity.setTimestamp(new Timestamp(System.currentTimeMillis()));
        }
        if (dto.getUsername() != null) {
            Employee employee = employeeRepository.getReferenceById(dto.getUsername());
            entity.setEmployee(employee);
        }
        return entity;
    }
}
