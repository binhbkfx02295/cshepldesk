package com.binhbkfx02295.cshelpdesk.employee_management.employee.service;

import com.binhbkfx02295.cshelpdesk.common.cache.MasterDataCache;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.EmployeeDTO;
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
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserGroupRepository userGroupRepository;
    private final PasswordEncoder passwordEncoder;
    private final StatusLogRepository statusLogRepository;
    private final MessageSource messageSource;
    private final MasterDataCache cache;
    private final EmployeeMapper mapper;

    @Override
    public APIResultSet<EmployeeDTO> createUser(EmployeeDTO employeeDTO) {
        if (cache.getEmployee(employeeDTO.getUsername()) != null) {
            log.info("check cache: user {} not exists yet", employeeDTO.getUsername());
            return APIResultSet.badRequest("Username already exists");
        }
        UserGroup group = cache.getUserGroup(employeeDTO.getGroupId());
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
            if (employeeDTO.getStatusLog() == null) {
                Status status = cache.getStatus("offline");
                StatusLog newLog = new StatusLog();
                newLog.setStatus(status);
                newLog.setEmployee(user);
                user.getStatusLogs().add(newLog);
            }

            user = employeeRepository.save(user);
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
        Optional<com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee> userOpt = employeeRepository.findById(username);
        if (userOpt.isEmpty()) {
            log.info("Update user: user not found");
            return APIResultSet.notFound("Update user: user not found");
        }

        Optional<UserGroup> groupOpt = userGroupRepository.findById(employeeDTO.getGroupId());
        if (groupOpt.isEmpty()) {
            return APIResultSet.badRequest("Invalid user group");
        }

        try {
            com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee user = userOpt.get();
            user.setName(employeeDTO.getName());
            user.setDescription(employeeDTO.getDescription());
            user.setUserGroup(groupOpt.get());

            employeeRepository.save(user);
            cache.updateEmployee(user);
            log.info(String.format("Update employee %s: success", employeeDTO.getUsername()));
            return APIResultSet.ok("User updated", employeeDTO);
        } catch (Exception e) {
            log.info(String.format("Update employee %s: failed", employeeDTO.getUsername()), e);
            return APIResultSet.internalError(String.format("Update employee %s: failed", employeeDTO.getUsername()));
        }
    }

    @Override
    public APIResultSet<Void> lockUser(String username) {
        Optional<Employee> userOpt = employeeRepository.findById(username);
        if (userOpt.isEmpty()) return APIResultSet.notFound("User not found");

        try {
           Employee user = userOpt.get();
            user.setActive(false);
            user = employeeRepository.save(user);
            cache.updateEmployee(user);
            log.info("Lock user {}: locked", user.getUsername());
            return APIResultSet.ok("User locked", null);
        } catch (Exception e) {

            log.info("Lock user {}: failed, internal server error: {}", username, e.getStackTrace());
            return APIResultSet.internalError("Lock user {}: failed, internal server error");
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
            return APIResultSet.ok(String.format("Find Employee %s: found", username), toDTO(employee));
        }

    }

    @Override
    public APIResultSet<List<EmployeeDTO>> getAllUsers() {
        try {
            List<Employee> users = cache.getAllEmployees().values().stream().toList();
            List<EmployeeDTO> result = users.stream().map(this::toDTO).toList();
            log.info("Find all employee: found");
            return APIResultSet.ok("All users", result);
        } catch (Exception e) {
            log.info("Find all employee: server error", e);
            return APIResultSet.internalError("Find all employee: server error");
        }
    }

    @Override
    public APIResultSet<List<EmployeeDTO>> getUsersByGroup(int groupId) {
        try {
            UserGroup group = cache.getUserGroup(groupId);
            if (group == null) {
                log.info("getUsersByGroup: group not found");
                return APIResultSet.notFound("getUsersByGroup: group not found");
            }
            List<Employee> users = cache.getAllEmployees().values().stream().filter(employee -> employee.getUserGroup().getGroupId() == groupId).toList();
            List<EmployeeDTO> result = users.stream().map(this::toDTO).toList();
            return APIResultSet.ok("getUsersByGroup: ok", result);
        } catch (Exception e) {
            log.info("getUsersByGroup: group not found", e);
            return APIResultSet.internalError("getUsersByGroup: group not found");
        }
    }

    @Override
    public APIResultSet<Void> changePassword(String username, String newPassword) {
        Optional<Employee> userOpt = employeeRepository.findById(username);
        if (userOpt.isEmpty()) return APIResultSet.notFound("User not found");

        try {
            Employee user = userOpt.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            employeeRepository.save(user);
            return APIResultSet.ok("Password updated", null);
        } catch (Exception e) {
            log.error("Change password error", e);
            return APIResultSet.internalError("Failed to change password");
        }
    }

    @Override
    public APIResultSet<Void> updateStatus(String username, boolean isActive) {
        Optional<Employee> userOpt = employeeRepository.findById(username);
        if (userOpt.isEmpty()) return APIResultSet.notFound("User not found");

        try {
            Employee user = userOpt.get();
            user.setActive(isActive);
            user = employeeRepository.save(user);
            cache.updateEmployee(user);
            return APIResultSet.ok("Status updated", null);
        } catch (Exception e) {
            log.error("Update status error", e);
            return APIResultSet.internalError("Failed to update status");
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
    public APIResultSet<List<EmployeeDTO>> findALlWithStatusLog() {
        try {
            List<Employee> list = employeeRepository.findAllWithTop1EmployeeStatusLog();
            List<EmployeeDTO> listDTO = list.stream().map(employee -> {
                EmployeeDTO employeeDTO = toDTO(employee);
                if (!employee.getStatusLogs().isEmpty()) {
                    List<StatusLogDTO> logs = new ArrayList<>();
                    for (StatusLog log : employee.getStatusLogs()) {
                        StatusLogDTO logDTO = new StatusLogDTO();
                        logDTO.setStatus(log.getStatus().getName());
                        logDTO.setFrom(log.getTimestamp());
                        logs.add(logDTO);
                    }
                    employeeDTO.setStatusLog(logs);
                }
                return employeeDTO;

            }).toList();


            return APIResultSet.ok("Lấy thành công", listDTO);

        } catch (Exception e) {
            log.error("Lỗi lấy user với status log ", e);
            return APIResultSet.internalError("Lỗi lấy user với status log ");
        }
    }

    @Override
    public APIResultSet<StatusLogDTO> findFirstStatusLogByUsername(String username) {
        log.info("finding latest log for {}", username);
        try {
            Optional<StatusLog> logOpt = statusLogRepository.findFirstByEmployee_UsernameOrderByTimestampDesc(username);
            if (logOpt.isPresent()) {
                StatusLog log = logOpt.get();
                StatusLogDTO logDTO = new StatusLogDTO();
                logDTO.setStatus(log.getStatus().getName());
                logDTO.setFrom(log.getTimestamp());
                return APIResultSet.ok(String.format("Get recent status log of %s: found", username), logDTO);
            } else {
                return APIResultSet.notFound(String.format("Get recent status log of %s: not found", username));
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
            List<StatusLog> logs = employee.get().getStatusLogs();
            return APIResultSet.ok(String.format("Get recent status log of %s: found", username), logs.stream().map(StatusLog::toDTO).toList());

//            Optional<StatusLog> logOpt = statusLogRepository.findFirstByEmployee_UsernameOrderByTimestampDesc(username);
//            if (logOpt.isPresent()) {
//                StatusLog log = logOpt.get();
//                StatusLogDTO logDTO = new StatusLogDTO();
//                logDTO.setStatus(log.getStatus().getName());
//                logDTO.setFrom(log.getTimestamp());
//                return APIResultSet.ok(String.format("Get recent status log of %s: found", username), logDTO);
//            } else {
//                return APIResultSet.notFound(String.format("Get recent status log of %s: not found", username));
//            }
        } catch (Exception e) {
            log.error("Lỗi lấy user với status log ", e);
            return APIResultSet.internalError("Lỗi lấy user với status log ");
        }
    }

    @Override
    public APIResultSet<Void> updateOnlineStatus(String username, StatusLogDTO logDTO) {

        try {
            Optional<com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee> employeeDTO = employeeRepository.findByUsername(username);
            if (employeeDTO.isPresent()) {
                com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee employee = employeeDTO.get();
                StatusLog newLog = new StatusLog();
                Status status = cache.getStatus(logDTO.getStatus());
                newLog.setStatus(status);
                newLog.setEmployee(employee);

                employee.getStatusLogs().add(newLog);
                employeeRepository.save(employee);
                log.info(String.format("Cập nhật status của %s thành công", username));
                return APIResultSet.ok(String.format("Cập nhật status của %s thành công", username), null);
            } else {
                return APIResultSet.ok(String.format("Lỗi username: %s không tồn tại", username), null);
            }
        } catch (Exception e) {
            return APIResultSet.internalError();
        }
    }

    private EmployeeDTO toDTO(Employee user) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setDescription(user.getDescription());
        dto.setGroupId(user.getUserGroup().getGroupId());

        return dto;
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
            APIResultSet<Void> result = APIResultSet.ok(String.format("Xoa user %s thanh cong", username), null);
            log.info(result.getMessage());
            return result;
        } catch (Exception e) {
            return APIResultSet.internalError();
        }

    }
}
