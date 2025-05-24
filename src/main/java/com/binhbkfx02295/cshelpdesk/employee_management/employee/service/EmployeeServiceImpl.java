package com.binhbkfx02295.cshelpdesk.employee_management.employee.service;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final MasterDataCache cache;
    private final EmployeeMapper mapper;

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
            Status status = cache.getStatus("offline");
            StatusLog newLog = new StatusLog();
            newLog.setStatus(status);
            newLog.setEmployee(user);
            user.getStatusLogs().add(newLog);

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

            Employee saved = employeeRepository.save(user);
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
            List<Employee> list = employeeRepository.findAllWithTop1EmployeeStatusLog();
            APIResultSet<List<EmployeeDashboardDTO>> result = APIResultSet.ok("Lay nhan vien dashboard thanh cong", list.stream().map(mapper::toDashboardDTO).toList());

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
        } catch (Exception e) {
            log.error("Lỗi lấy user với status log ", e);
            return APIResultSet.internalError("Lỗi lấy user với status log ");
        }
    }

    @Override
    public APIResultSet<Void> updateOnlineStatus(String username, StatusLogDTO logDTO) {

        try {
            Employee employee = cache.getEmployee(username);
            StatusLog newLog;
            Optional<StatusLog> latestLog;
            if (employee != null) {
                newLog = new StatusLog();
                latestLog = statusLogRepository.findFirstByEmployee_UsernameOrderByTimestampDesc(username);
                //checking if duplicate status, then skip
                if (latestLog.isPresent() && !latestLog.get().getStatus().getName().equalsIgnoreCase(logDTO.getStatus())) {
                    newLog.setStatus(cache.getStatus(logDTO.getStatus()));
                    newLog.setEmployee(employee);
                    statusLogRepository.save(newLog);
                    log.info(String.format("Cập nhật status của %s thành công", username));
                    return APIResultSet.ok(String.format("Cập nhật status của %s thành công", username), null);
                } else {
                    return APIResultSet.badRequest("Trùng status, không cập nhật");
                }
            } else {
                newLog = null;
                return APIResultSet.ok(String.format("Lỗi username: %s không tồn tại", username), null);
            }
        } catch (Exception e) {
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
            cache.getAllEmployees().remove(username);
            APIResultSet<Void> result = APIResultSet.ok(String.format("Xoa user %s thanh cong", username), null);
            log.info(result.getMessage());
            return result;
        } catch (Exception e) {
            return APIResultSet.internalError();
        }

    }
}
