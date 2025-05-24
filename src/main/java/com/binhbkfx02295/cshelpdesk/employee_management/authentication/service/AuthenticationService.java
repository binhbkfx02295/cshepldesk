package com.binhbkfx02295.cshelpdesk.employee_management.authentication.service;

import com.binhbkfx02295.cshelpdesk.employee_management.authentication.dto.LoginResponseDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.authentication.util.ValidationResult;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.EmployeeDTO;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.employee_management.authentication.dto.LoginRequestDTO;


import java.util.Locale;

public interface AuthenticationService {

    /**
     * Đăng nhập với username và password
     * @param request chứa username và password
     * @return APIResultSet<LoginResponseDTO>: Nếu lỗi validate -> BAD_REQUEST với ValidationResult,
     * Nếu sai thông tin đăng nhập -> OK với message lỗi và data null.
     * Nếu thành công -> OK với message thành công và LoginResponseDTO chứa thông tin user
     */
    APIResultSet<LoginResponseDTO> login(LoginRequestDTO request);

    /**
     * Đăng xuất và huỷ session hiện tại
     * @return APIResultSet<Void>
     */
    APIResultSet<Void> logout(EmployeeDTO employeeDTO);
    /**
     * Chỉ dùng để kiểm tra lỗi validate khi cần test
     * @param request request login
     * @param locale locale hiện tại
     * @return ValidationResult
     */
    ValidationResult validate(LoginRequestDTO request, Locale locale);
}
