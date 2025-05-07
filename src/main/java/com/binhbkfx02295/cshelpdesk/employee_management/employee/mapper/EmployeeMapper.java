package com.binhbkfx02295.cshelpdesk.employee_management.employee.mapper;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.EmployeeDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class EmployeeMapper {
    public EmployeeDTO toDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setUsername(employee.getUsername());
        dto.setDescription(employee.getDescription());
        dto.setGroupId(employee.getUserGroup().getGroupId());
        return dto;
    }

    ;

    public Employee toEntity(EmployeeDTO employeedto) {
        return null;
    }

    ;


}
