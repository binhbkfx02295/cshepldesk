package com.binhbkfx02295.cshelpdesk.websocket.event;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.StatusLogDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.EmployeeDTO;

@Getter
@RequiredArgsConstructor
public class EmployeeEvent {
    public enum Action {CREATED, UPDATED}
    private final Action action;
    private final EmployeeDTO employeeDTO;
}