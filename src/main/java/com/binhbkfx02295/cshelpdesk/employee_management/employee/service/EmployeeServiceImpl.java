package com.binhbkfx02295.cshelpdesk.employee_management.employee.service;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.*;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.mapper.EmployeeDetailDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.mapper.StatusLogMapper;
import com.binhbkfx02295.cshelpdesk.infrastructure.common.cache.MasterDataCache;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Status;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.StatusLog;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.mapper.EmployeeMapper;
import com.binhbkfx02295.cshelpdesk.employee_management.usergroup.UserGroupRepository;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.repository.EmployeeRepository;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.PasswordValidator;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import com.binhbkfx02295.cshelpdesk.websocket.event.EmployeeEvent;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserGroupRepository userGroupRepository;
    private final PasswordEncoder passwordEncoder;
    private final MasterDataCache cache;
    private final EmployeeMapper mapper;
    private final StatusLogMapper statusLogMapper;
    private final ApplicationEventPublisher publisher;
    private final EntityManager entityManager;

    @Override
    public APIResultSet<EmployeeDTO> createUser(EmployeeDTO employeeDTO) {
        APIResultSet<EmployeeDTO> result;
        if (cache.getEmployee(employeeDTO.getUsername()) != null) {
            result = APIResultSet.badRequest("Tên tài khoản đã tồn tại");
        } else if (cache.getUserGroup(employeeDTO.getUserGroup().getGroupId()) == null){
            result = APIResultSet.badRequest("Nhóm người dùng không hợp lệ");
        } else if (PasswordValidator.validate(employeeDTO.getPassword()) != null){
            result = APIResultSet.badRequest(PasswordValidator.VALIDATION_ERROR);
        } else {
            try {
                Employee user = mapper.toEntity(employeeDTO);
                user.setPassword(passwordEncoder.encode(user.getPassword()));

                //add status log
                user.setUserGroup(userGroupRepository.getReferenceById(employeeDTO.getUserGroup().getGroupId()));
                Status status = cache.getStatus(3);
                StatusLog newLog = new StatusLog();
                newLog.setStatus(status);
                newLog.setEmployee(user);
                user.getStatusLogs().add(newLog);

                user = employeeRepository.save(user);
                entityManager.flush();
                entityManager.clear();
                cache.updateEmployee(user);
                result = APIResultSet.ok(String.format("User %s created", user.getUsername()), mapper.toDTO(user));

            } catch (Exception e) {
                log.info("loi create user ", e);
                result = APIResultSet.internalError("Internal error while creating user");
            }
        }
        log.info(result.getMessage());
        return result;
    }

    @Override
    public APIResultSet<EmployeeDetailDTO> updateUser(String username, EmployeeDTO employeeDTO) {
        Employee user = cache.getEmployee(username);
        if (user == null) {
            log.info("Update user: user not found");
            return APIResultSet.notFound("Update user: user not found");
        }

        try {
            mergeToUser(user, employeeDTO);
            Employee saved = employeeRepository.save(user);
            entityManager.flush();
            entityManager.clear();
            cache.updateEmployee(saved);
            log.info(String.format("Update employee %s: success", employeeDTO.getUsername()));
            return APIResultSet.ok("User updated", mapper.toDetailDTO(cache.getEmployee(employeeDTO.getUsername())));
        } catch (Exception e) {
            log.info(String.format("Update employee %s: failed", employeeDTO.getUsername()), e);
            return APIResultSet.internalError(String.format("Update employee %s: failed", employeeDTO.getUsername()));
        }
    }

    private void mergeToUser(Employee user, EmployeeDTO employeeDTO) {
        user.setActive(employeeDTO.isActive());

        if (employeeDTO.getUserGroup() != null && user.getUserGroup() != null && employeeDTO.getUserGroup().getGroupId() !=
                user.getUserGroup().getGroupId()) {
            user.setUserGroup(cache.getUserGroup(employeeDTO.getUserGroup().getGroupId()));
        }

        if (employeeDTO.getName() != null && !employeeDTO.getName().equalsIgnoreCase(user.getName())) {
            user.setName(employeeDTO.getName());
        }

        if (employeeDTO.getDescription() != null && !employeeDTO.getDescription().equalsIgnoreCase(user.getDescription())) {
            user.setDescription(employeeDTO.getDescription());
        }

        if (employeeDTO.getEmail() != null && !employeeDTO.getEmail().equalsIgnoreCase(user.getEmail())) {
            user.setEmail(employeeDTO.getEmail());
        }

        if (employeeDTO.getPhone() != null && !employeeDTO.getPhone().equalsIgnoreCase(user.getPhone())) {
            user.setPhone(employeeDTO.getPhone());
        }

        if (employeeDTO.getStatusLogs() != null && !employeeDTO.getStatusLogs().isEmpty()) {
            user.getStatusLogs().add(statusLogMapper.toEntity(employeeDTO.getStatusLogs().get(0)));
        }
    }

    @Override
    public APIResultSet<EmployeeDTO> getUserByUsername(String username) {
        Employee employee = cache.getEmployee(username);

        if (employee == null) {
            log.info(String.format("Find Employee %s: not found", username));
            return APIResultSet.notFound(String.format("Find Employee %s: not found", username));
        } else {
            log.info(String.format("Find Employee %s: found", username));
            return APIResultSet.ok(String.format("Find Employee %s: found", username), mapper.toDTO(employee));
        }

    }

    @Override
    public APIResultSet<List<EmployeeDTO>> getAllUsers() {
        try {
            List<Employee> result = cache.getAllEmployees().values().stream().toList();
            log.info("Find all employee: found");
            return APIResultSet.ok("All users", result.stream().map(mapper::toDTO).toList());
        } catch (Exception e) {
            log.info("Find all employee: server error", e);
            return APIResultSet.internalError("Find all employee: server error");
        }
    }

    @Override
    public APIResultSet<Void> changePassword(String username, String password, String newPassword) {

        try {
            Optional<Employee> userOpt = employeeRepository.findById(username);
            if (userOpt.isEmpty()) return APIResultSet.unauthorized("Sai mật khẩu hoặc tên đăng nhập");

            Employee user = userOpt.get();
            //validate password
            if (passwordEncoder.matches(password, user.getPassword())) {
                APIResultSet.unauthorized("Sai mật khẩu hoặc tên đăng nhập");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
            employeeRepository.save(user);
            return APIResultSet.ok("Password updated", null);
        } catch (Exception e) {
            log.error("Change password error", e);
            return APIResultSet.internalError("Failed to change password");
        }
    }

    @Override
    public APIResultSet<Void> existsByUsername(String username) {
        try {
            Employee employee = cache.getEmployee(username);
            if (employee != null) {
                return APIResultSet.ok("Staff có tồn tại", null);
            } else {
                return APIResultSet.notFound("Staff không tồn tại");
            }
        } catch (Exception e) {
            log.error("Lỗi server không thể tìm kiếm staff", e);
            return APIResultSet.internalError("Lỗi server không thể tìm kiếm staff");
        }
    }

    @Override
    public APIResultSet<List<EmployeeDashboardDTO>> getForDashboard() {
        try {
            List<Ticket> tickets = cache.getDashboardTickets().values().stream().toList();
            Map<String, EmployeeDashboardDTO> list = cache.getAllEmployees().values().stream().map(
                    mapper::toDashboardDTO).collect(Collectors.toMap(EmployeeDashboardDTO::getUsername, Function.identity()));
            for (Ticket ticket : tickets) {
                if (ticket.getAssignee() != null) {
                    EmployeeDashboardDTO target = list.get(ticket.getAssignee().getUsername());
                    if (ticket.getProgressStatus().getId() != 3) {
                        target.setTicketCount(target.getTicketCount() + 1);
                    }
                }

            }
            APIResultSet<List<EmployeeDashboardDTO>> result = APIResultSet.ok("Lay nhan vien dashboard thanh cong", list.values().stream().toList());
            log.info(result.getMessage());

            return result;

        } catch (Exception e) {
            log.error("Lỗi lấy user với status log ", e);
            return APIResultSet.internalError("Lỗi lấy user với status log ");
        }
    }

    @Override
    public APIResultSet<StatusLogDTO> getLatestOnlineStatus(String username) {
        log.info("finding latest log for {}", username);
        APIResultSet<StatusLogDTO> result;
        try {
            StatusLog statusLog = cache.getEmployee(username).getStatusLogs().get(
                    cache.getEmployee(username).getStatusLogs().size() - 1
            );
            result = APIResultSet.ok("Recent status OK", statusLogMapper.toDTO(statusLog));
            log.info(result.getMessage());

        } catch (Exception e) {
            log.error("Lỗi lấy user với status log ", e);
            return APIResultSet.internalError("Lỗi lấy user với status log ");
        }
        return result;
    }


    @Override
    public APIResultSet<List<StatusLogDTO>> findWithAllLogs(EmployeeDTO employeeDTO) {
        String username = employeeDTO.getUsername();
        log.info("finding recent logs for {}", username);
        try {
            Optional<Employee> employee = employeeRepository.findByUsername(username);
            if (employee.isPresent()) {
                List<StatusLog> logs = employee.get().getStatusLogs();
                return APIResultSet.ok(String.format("Get recent status log of %s: found", username),
                        logs.stream().map(statusLogMapper::toDTO).toList());
            } else {
                return APIResultSet.notFound(String.format("Recent status of %s log not found", username));
            }

        } catch (Exception e) {
            log.error("Lỗi lấy user với status log ", e);
            return APIResultSet.internalError("Lỗi lấy user với status log ");
        }
    }

    @Override
    public APIResultSet<Void> updateOnlineStatus(StatusLogDTO logDTO) {

        try {
            Optional<Employee> employeeOtp = employeeRepository.findWithTop1StatusLog(logDTO.getUsername());
            StatusLog latestLog;
            if (employeeOtp.isPresent()) {
                Employee employee = employeeOtp.get();
                latestLog = employee.getStatusLogs().get(0);
                if (latestLog.getStatus().getId() != (logDTO.getStatus().getId())) {
                    StatusLog temp = statusLogMapper.toEntity(logDTO);
                    employee.getStatusLogs().add(temp);
                    employeeRepository.save(employee);
                    entityManager.flush();
                    entityManager.clear();
                    cache.updateAllEmployees();
                    //TODO: broad cast event
                    publisher.publishEvent(new EmployeeEvent(EmployeeEvent.Action.UPDATED, mapper.toDTO(cache.getEmployee(logDTO.getUsername()))));
                    return APIResultSet.ok(String.format("Cập nhật status của %s thành công", logDTO.getUsername()), null);
                } else {
                    return APIResultSet.badRequest("Trùng status, không cập nhật");
                }
            } else {
                return APIResultSet.ok(String.format("Lỗi username: %s không tồn tại", logDTO.getUsername()), null);
            }
        } catch (Exception e) {
            log.info("Loi cap nhat online status", e);
            return APIResultSet.internalError();
        }
    }


    public APIResultSet<List<StatusDTO>> getAllOnlineStatus() {
        try {
            log.info("Lấy online statuses thành công");
            return APIResultSet.ok("Lấy online statuses thành công", cache.getAllStatus().values().stream().map(
                    status -> {
                        StatusDTO statusDTO = new StatusDTO();
                        statusDTO.setId(status.getId());
                        statusDTO.setName(status.getName());
                        return statusDTO;
                    }
            ).toList());
        } catch (Exception e) {
            log.error("Lấy danh sách status bị lỗi ", e);
            return APIResultSet.internalError();
        }
    }

    @Override
    public APIResultSet<Void> deleteByUsername(String username) {
        try {
            employeeRepository.deleteByUsername(username);
            entityManager.flush();
            entityManager.clear();
            cache.getAllEmployees().remove(username);
            APIResultSet<Void> result = APIResultSet.ok(String.format("Xóa nhân viên thành công: %s", username), null);
            log.info(result.getMessage());
            return result;
        } catch (Exception e) {
            return APIResultSet.internalError();
        }

    }

    @Override
    public APIResultSet<Void> resetPassword(ResetPasswordDTO resetPasswordDTO) {
        String validationErrorString = PasswordValidator.validate(resetPasswordDTO.getDefaultPassword());
        APIResultSet<Void> result;
        if (validationErrorString != null) {
            //if return validation error as String..
            result = APIResultSet.badRequest(validationErrorString);
        } else {
            Optional<Employee> employeeOpt = employeeRepository.findById(resetPasswordDTO.getUsername());
            if (employeeOpt.isPresent()) {
                employeeOpt.get().setPassword(passwordEncoder.encode(resetPasswordDTO.getDefaultPassword()));
                employeeRepository.save(employeeOpt.get());
                result = APIResultSet.ok("Đặt lại mật khẩu thành công", null);
            } else {
                result = APIResultSet.badRequest(String.format("Nhân viên không tồn tại: %s", resetPasswordDTO.getUsername()));
            }
        }
        log.info(result.getMessage());
        return result;
    }


}
