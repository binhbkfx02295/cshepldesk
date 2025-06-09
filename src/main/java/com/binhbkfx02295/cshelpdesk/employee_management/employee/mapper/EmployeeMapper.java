package com.binhbkfx02295.cshelpdesk.employee_management.employee.mapper;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.*;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.StatusLog;
import com.binhbkfx02295.cshelpdesk.employee_management.usergroup.UserGroupMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

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
        dto.setPhone(employee.getPhone());
        dto.setEmail(employee.getEmail());
        dto.setUserGroup(userGroupMapper.toDTO(employee.getUserGroup()));
        dto.setStatusLogs(employee.getStatusLogs().stream().map(statusLogMapper::toDTO).toList());
        dto.setCreatedAt(Timestamp.from(employee.getCreatedAt()));
        return dto;
    }

    public EmployeeTicketDTO toTicketDTO(Employee employee) {
        EmployeeTicketDTO dto = new EmployeeTicketDTO();
        dto.setUsername(employee.getUsername());
        dto.setName(employee.getName());
        dto.setGroup(userGroupMapper.toDTO(employee.getUserGroup()));
        return dto;
    }
    public Employee toEntity(EmployeeDTO dto)  {
        Employee entity = new Employee();
        entity.setUsername(dto.getUsername());
        entity.setPassword(dto.getPassword());
        entity.setUserGroup(userGroupMapper.toEntity(dto.getUserGroup()));
        entity.setActive(dto.isActive());
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setDescription(dto.getDescription());
        return entity;
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


    public EmployeeDetailDTO toDetailDTO(Employee entity) {
        EmployeeDetailDTO dto = new EmployeeDetailDTO();
        dto.setDescription(entity.getDescription());
        dto.setName(entity.getName());
        dto.setUsername(entity.getUsername());
        dto.setUserGroup(userGroupMapper.toDTO(entity.getUserGroup()));
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setActive(entity.isActive());
        dto.setCreatedAt(Timestamp.from(entity.getCreatedAt()));
        return dto;
    }
}
