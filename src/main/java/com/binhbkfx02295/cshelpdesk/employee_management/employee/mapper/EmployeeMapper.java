package com.binhbkfx02295.cshelpdesk.employee_management.employee.mapper;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.*;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.StatusLog;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Component
@RequiredArgsConstructor
public class EmployeeMapper {

    private final StatusLogMapper statusLogMapper;
    private final UserGroupMapper userGroupMapper;

    public EmployeeDTO toDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setUsername(employee.getUsername());
        dto.setDescription(employee.getDescription());
        dto.setName(employee.getName());
        dto.setActive(employee.isActive());
        dto.setUserGroup(userGroupMapper.toDTO(employee.getUserGroup()));
        dto.setStatusLogs(employee.getStatusLogs().stream().map(statusLogMapper::toDTO).toList());
        return dto;
    }

    public EmployeeTicketDTO toTicketDTO(Employee employee) {
        EmployeeTicketDTO dto = new EmployeeTicketDTO();
        dto.setUsername(employee.getUsername());
        dto.setName(employee.getName());
        return dto;
    }
    public Employee toEntity(EmployeeDTO employeedto)  {
        return null;
    }

    public Employee toEntity(EmployeeStatusDTO dto) {
        Employee entity = new Employee();
        entity.setUsername(dto.getUsername());
        return entity;
    }

    public EmployeeDashboardDTO toDashboardDTO(Employee entity) {
        EmployeeDashboardDTO dto = new EmployeeDashboardDTO();
        dto.setDescription(entity.getDescription());
        dto.setName(entity.getName());
        dto.setUsername(entity.getUsername());
        dto.setUserGroup(userGroupMapper.toDTO(entity.getUserGroup()));
        StatusLog statusLog = entity.getStatusLogs().get(0);
        StatusLogDTO statusLogDTO = statusLogMapper.toDTO(statusLog);
        dto.setStatusLog(statusLogDTO);
        return dto;
    }

    public EmployeeStatusDTO toStatusDTO(Employee employee) {
        EmployeeStatusDTO dto = new EmployeeStatusDTO();
        dto.setUsername(employee.getUsername());
        return dto;
    }


}
