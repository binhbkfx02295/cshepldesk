package com.binhbkfx02295.cshelpdesk.employee_management.employee.mapper;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.EmployeeDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.EmployeeDashboardDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.StatusLogDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.StatusLog;
import com.binhbkfx02295.cshelpdesk.employee_management.usergroup.UserGroupDTO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Data
@Component
@RequiredArgsConstructor
public class EmployeeMapper {

    public EmployeeDTO toDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setUsername(employee.getUsername());
        dto.setDescription(employee.getDescription());
        dto.setName(employee.getName());
        dto.setActive(employee.isActive());

        if (employee.getUserGroup() != null) {
            dto.setGroupId(employee.getUserGroup().getGroupId());
        }

        return dto;
    }
    public Employee toEntity(EmployeeDTO employeedto)  {
        return null;
    }

    public EmployeeDashboardDTO toDashboardDTO(Employee entity) {
        EmployeeDashboardDTO dto = new EmployeeDashboardDTO();
        dto.setUsername(entity.getUsername());
        dto.setDescription(entity.getDescription());
        dto.setName(entity.getName());
        dto.setActive(entity.isActive());

        UserGroupDTO userGroupDTO = new UserGroupDTO();
        userGroupDTO.setGroupId(entity.getUserGroup().getGroupId());
        userGroupDTO.setDescription(entity.getUserGroup().getDescription());
        userGroupDTO.setName(entity.getUserGroup().getName());

        dto.setUserGroup(userGroupDTO);

        StatusLog statusLog = entity.getStatusLogs().get(0);
        StatusLogDTO statusLogDTO = new StatusLogDTO();
        List<StatusLogDTO> list = new ArrayList<>();
        statusLogDTO.setFrom(statusLog.getTimestamp());
        statusLogDTO.setStatus(statusLog.getStatus().getName());
        list.add(statusLogDTO);
        dto.setStatusLog(list);

        return dto;

    }



}
