
package com.binhbkfx02295.cshelpdesk.employee_management.authentication.service;

import com.binhbkfx02295.cshelpdesk.common.cache.MasterDataCache;
import com.binhbkfx02295.cshelpdesk.employee_management.authentication.dto.LoginRequestDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.authentication.dto.LoginResponseDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.authentication.util.ValidationHelper;
import com.binhbkfx02295.cshelpdesk.employee_management.authentication.util.ValidationResult;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.StatusLog;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.repository.EmployeeRepository;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.repository.StatusLogRepository;
import com.binhbkfx02295.cshelpdesk.employee_management.permission.Permission;
import com.binhbkfx02295.cshelpdesk.employee_management.usergroup.UserGroup;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock private EmployeeRepository employeeRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private ValidationHelper validationHelper;
    @Mock private MessageSource messageSource;
    @Mock private StatusLogRepository statusLogRepository;
    @Mock private MasterDataCache cache;

    @InjectMocks private AuthenticationServiceImpl authenticationService;

    @Captor private ArgumentCaptor<Employee> employeeCaptor;

    private final Locale locale = Locale.forLanguageTag("vi");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("login validate failed")
    void login_validate_failed() {
        LoginRequestDTO request = new LoginRequestDTO("ab", "123");
        ValidationResult vr = new ValidationResult();
        vr.addError("username", "Tên đăng nhập không hợp lệ");
        vr.addError("password", "Mật khẩu không hợp lệ");

        when(validationHelper.validateLoginInput(any(), any(), any())).thenReturn(vr);

        APIResultSet<LoginResponseDTO> result = authenticationService.login(request);

        assertThat(result.getHttpCode()).isEqualTo(400);
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getValidationResult()).isEqualTo(vr);
    }

    @Test
    @DisplayName("login validate success")
    void login_validate_success() {
        LoginRequestDTO request = new LoginRequestDTO("validUser", "Password@123");
        ValidationResult vr = new ValidationResult(); // không lỗi

        when(validationHelper.validateLoginInput(any(), any(), any())).thenReturn(vr);
        when(employeeRepository.findById("validUser")).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("auth.invalid.credentials"), any(), any())).thenReturn("Sai tên đăng nhập hoặc mật khẩu");

        APIResultSet<LoginResponseDTO> result = authenticationService.login(request);

        assertThat(result.getHttpCode()).isEqualTo(400); // login failed vì user không tồn tại
        assertThat(result.getData()).isNull();
        assertThat(result.getMessage()).contains("Sai tên đăng nhập hoặc mật khẩu");
    }

    @Test
    @DisplayName("login username not exists failed")
    void login_username_not_exists() {
        LoginRequestDTO request = new LoginRequestDTO("unknown", "Password@123");
        when(validationHelper.validateLoginInput(any(), any(), any())).thenReturn(new ValidationResult());
        when(employeeRepository.findById("unknown")).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("auth.invalid.credentials"), any(), any())).thenReturn("Sai tên đăng nhập hoặc mật khẩu");

        APIResultSet<LoginResponseDTO> result = authenticationService.login(request);

        assertThat(result.getHttpCode()).isEqualTo(400);
        assertThat(result.getData()).isNull();
        assertThat(result.getMessage()).isEqualTo("Sai tên đăng nhập hoặc mật khẩu");
    }

    @Test
    @DisplayName("login username exists but wrong password 1 time failed")
    void login_wrong_password_once() {
        Employee employee = new Employee();
        employee.setUsername("user");
        employee.setPassword("hashed@12");
        employee.setActive(true);
        employee.setFailedLoginCount(0);

        when(validationHelper.validateLoginInput(any(), any(), any())).thenReturn(new ValidationResult());
        when(employeeRepository.findWithUserGroupAndPermissionsByUsername("user")).thenReturn(Optional.of(employee));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        APIResultSet<LoginResponseDTO> result = authenticationService.login(new LoginRequestDTO("user", "wronss@1"));

        assertThat(result.getHttpCode()).isEqualTo(401);
        assertThat(result.getData()).isNull();
        verify(employeeRepository).save(employeeCaptor.capture());
        assertThat(employeeCaptor.getValue().getFailedLoginCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("login username exists but wrong password 5 time failed → locked")
    void login_wrong_password_five_times_locks_account() {
        Employee employee = new Employee();
        employee.setUsername("user");
        employee.setPassword("hashed@12");
        employee.setActive(true);
        employee.setFailedLoginCount(4);

        when(validationHelper.validateLoginInput(any(), any(), any())).thenReturn(new ValidationResult());
        when(employeeRepository.findWithUserGroupAndPermissionsByUsername("user")).thenReturn(Optional.of(employee));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        APIResultSet<LoginResponseDTO> result = authenticationService.login(new LoginRequestDTO("user", "wrong@123"));

        assertThat(result.getHttpCode()).isEqualTo(401);
        assertThat(result.getData()).isNull();
        verify(employeeRepository).save(employeeCaptor.capture());
        assertThat(employeeCaptor.getValue().isActive()).isFalse();
    }

    @Test
    @DisplayName("login username exists acc locked failed")
    void login_account_locked() {
        Employee employee = new Employee();
        employee.setUsername("user");
        employee.setPassword("hashed");
        employee.setActive(false);

        when(validationHelper.validateLoginInput(any(), any(), any())).thenReturn(new ValidationResult());
        when(employeeRepository.findWithUserGroupAndPermissionsByUsername("user")).thenReturn(Optional.of(employee));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        APIResultSet<LoginResponseDTO> result = authenticationService.login(new LoginRequestDTO("user", "Password@123"));

        assertThat(result.getHttpCode()).isEqualTo(403);
        assertThat(result.getData()).isNull();
        // account locked thì message giống invalid credential
    }

    @Test
    @DisplayName("login username success")
    void login_success() {


        Permission p1 = new Permission();
        p1.setId(1);

        Set<Permission> ps = new HashSet<>();
        ps.add(p1);

        UserGroup group = new UserGroup();
        group.setName("staff");
        group.setGroupId(1);
        group.setDescription("test");
        group.setPermissions(ps);

        Employee employee = new Employee();
        employee.setUsername("user");
        employee.setPassword("Abcd@1234");
        employee.setActive(true);
        employee.setFailedLoginCount(3);
        employee.setUserGroup(group);



        when(validationHelper.validateLoginInput(any(), any(), any())).thenReturn(new ValidationResult());
        when(employeeRepository.findWithUserGroupAndPermissionsByUsername("user")).thenReturn(Optional.of(employee));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(messageSource.getMessage(eq("auth.login.success"), any(), any())).thenReturn("Đăng nhập thành công");

        APIResultSet<LoginResponseDTO> result = authenticationService.login(new LoginRequestDTO("user", "Password@123"));

        assertThat(result.getHttpCode()).isEqualTo(200);
        assertThat(result.getData()).isNotNull();
        assertThat(result.getMessage()).isEqualTo("Đăng nhập thành công");
    }

}
