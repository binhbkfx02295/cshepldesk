package com.binhbkfx02295.cshelpdesk.employee_management.employee.service;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.mapper.StatusLogMapper;
import com.binhbkfx02295.cshelpdesk.infrastructure.common.cache.MasterDataCache;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.EmployeeDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.EmployeeDashboardDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.StatusDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.StatusLogDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Status;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.StatusLog;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.mapper.EmployeeMapper;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.repository.StatusLogRepository;
import com.binhbkfx02295.cshelpdesk.employee_management.usergroup.UserGroup;
import com.binhbkfx02295.cshelpdesk.employee_management.usergroup.UserGroupRepository;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.repository.EmployeeRepository;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.mapper.TicketMapper;
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
    private final StatusLogRepository statusLogRepository;
    private final MasterDataCache cache;
    private final EmployeeMapper mapper;
    private final StatusLogMapper statusLogMapper;
    private final ApplicationEventPublisher publisher;
    private final TicketMapper ticketMapper;
    private final EntityManager entityManager;

    @Override
    public APIResultSet<EmployeeDTO> createUser(EmployeeDTO employeeDTO) {
        if (cache.getEmployee(employeeDTO.getUsername()) != null) {
            log.info("check cache: user {} not exists yet", employeeDTO.getUsername());
            return APIResultSet.badRequest("Username already exists");
        }
        UserGroup group = cache.getUserGroup(employeeDTO.getUserGroup().getGroupId());
        if (group == null) {
            return APIResultSet.badRequest("Invalid user group");
        }

        try {

            Employee user = new Employee();
            user.setUsername(employeeDTO.getUsername());
            user.setName(employeeDTO.getName());
            user.setPassword(passwordEncoder.encode(employeeDTO.getPassword()));
            user.setUserGroup(group);
            user.setDescription(employeeDTO.getDescription());
            user.setActive(true);
            user.setFailedLoginCount(0);

            //add status log
            Status status = cache.getStatus(3);
            StatusLog newLog = new StatusLog();
            newLog.setStatus(status);
            newLog.setEmployee(user);
            user.getStatusLogs().add(newLog);

            user = employeeRepository.save(user);
            entityManager.flush();
            entityManager.clear();
            cache.updateEmployee(user);
            APIResultSet<EmployeeDTO> result = APIResultSet.ok(String.format("User %s created", user.getUsername()), mapper.toDTO(user));
            log.info(result.getMessage());
            return result;

        } catch (Exception e) {
            log.error("Failed to create user", e);
            return APIResultSet.internalError("Internal error while creating user");
        }
    }

    @Override
    public APIResultSet<EmployeeDTO> updateUser(String username, EmployeeDTO employeeDTO) {
        Optional<Employee> userOpt = employeeRepository.findById(username);
        if (userOpt.isEmpty()) {
            log.info("Update user: user not found");
            return APIResultSet.notFound("Update user: user not found");
        }

        Optional<UserGroup> groupOpt = userGroupRepository.findById(employeeDTO.getUserGroup().getGroupId());
        if (groupOpt.isEmpty()) {
            return APIResultSet.badRequest("Invalid user group");
        }

        try {
            Employee user = userOpt.get();
            user.setName(employeeDTO.getName());
            user.setDescription(employeeDTO.getDescription());
            user.setUserGroup(groupOpt.get());
            user.setEmail(employeeDTO.getEmail());
            user.setPhone(employeeDTO.getPhone());

            Employee saved = employeeRepository.save(user);
            entityManager.flush();
            entityManager.clear();
            cache.updateEmployee(saved);
            log.info(String.format("Update employee %s: success", employeeDTO.getUsername()));
            return APIResultSet.ok("User updated", mapper.toDTO(saved));
        } catch (Exception e) {
            log.info(String.format("Update employee %s: failed", employeeDTO.getUsername()), e);
            return APIResultSet.internalError(String.format("Update employee %s: failed", employeeDTO.getUsername()));
        }
    }
    @Override
    public APIResultSet<EmployeeDTO> getUserByUsername(String username) {
        Employee employee = cache.getAllEmployees().getOrDefault(username, null);

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
            for (Ticket ticket: tickets) {
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
        try {
            Optional<Employee> employeeOtp =  employeeRepository.findWithTop1StatusLog(username);

            if (employeeOtp.isPresent()) {
                StatusLogDTO dto = statusLogMapper.toDTO(employeeOtp.get().getStatusLogs().get(0));
                String msg = String.format("Get recent status log of %s: found", username);
                return APIResultSet.ok(msg, dto);
            } else {
                String msg = String.format("Get recent status log of %s: not found", username);
                return APIResultSet.notFound(msg);
            }
        } catch (Exception e) {
            log.error("Lỗi lấy user với status log ", e);
            return APIResultSet.internalError("Lỗi lấy user với status log ");
        }
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
                return APIResultSet.notFound(String.format("Recent status of %s log not found",username));
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
                //checking if duplicate status, then skip
                latestLog = employee.getStatusLogs().get(0);
                if (latestLog.getStatus().getId() != (logDTO.getStatus().getId())) {
                    StatusLog temp = statusLogMapper.toEntity(logDTO);
                    employee.getStatusLogs().add(temp);
                    employee = employeeRepository.save(employee);
                    entityManager.flush();
                    entityManager.clear();
                    log.info(String.format("Cập nhật status của %s thành công", logDTO.getUsername()));
                    log.info("truoc khi cap nhat cache: {}", mapper.toDashboardDTO(cache.getEmployee(logDTO.getUsername())));
                    cache.updateAllEmployees();
                    log.info("sau khi cap nhat cache: {}", mapper.toDashboardDTO(cache.getEmployee(logDTO.getUsername())));
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
            APIResultSet<Void> result = APIResultSet.ok(String.format("Xoa user %s thanh cong", username), null);
            log.info(result.getMessage());
            return result;
        } catch (Exception e) {
            return APIResultSet.internalError();
        }

    }
}
