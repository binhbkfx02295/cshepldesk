package com.binhbkfx02295.cshelpdesk.common.seed;

import com.binhbkfx02295.cshelpdesk.common.cache.MasterDataCache;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Status;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.StatusLog;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.repository.EmployeeRepository;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.repository.StatusRepository;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.service.EmployeeServiceImpl;
import com.binhbkfx02295.cshelpdesk.employee_management.permission.Permission;
import com.binhbkfx02295.cshelpdesk.employee_management.permission.PermissionRepository;
import com.binhbkfx02295.cshelpdesk.employee_management.usergroup.UserGroup;
import com.binhbkfx02295.cshelpdesk.employee_management.usergroup.UserGroupRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.entity.Emotion;
import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.entity.Satisfaction;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.entity.ProgressStatus;
import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.repository.EmotionRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.repository.SatisfactionRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.repository.ProgressStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class MasterDataSeeder implements CommandLineRunner {

    private final ProgressStatusRepository progressStatusRepository;
    private final EmotionRepository emotionRepository;
    private final SatisfactionRepository satisfactionRepository;
    private final EmployeeServiceImpl employeeService;
    private final StatusRepository  statusRepository;
    private final UserGroupRepository userGroupRepository;
    private final PermissionRepository permissionRepository;
    private final EmployeeRepository employeeRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MasterDataCache cache;


    @Override
    public void run(String... args) {
        seedPermission();
        seedUserGroup();
        seedStatus();
        seedEmployee();
        seedProgressStatuses();
        seedCustomerEmotions();
        seedCustomerSatisfactions();

        log.info("seeding done");
        cache.refresh();
    }

    private void seedPermission() {
        addPermissionIfMissing("VIEW_DASHBOARD");
        addPermissionIfMissing("VIEW_TICKET_ALL" );
        addPermissionIfMissing("VIEW_TICKET_DETAIL");
        addPermissionIfMissing("VIEW_EMPLOYEE_ALL" );
        addPermissionIfMissing("VIEW_EMPLOYEE_DASHBOARD");
    }


    private void seedUserGroup() {
        Set<Permission> permissions = new HashSet<>();
        permissions.add(new Permission(1, "", ""));
        permissions.add(new Permission(2, "", ""));
        permissions.add(new Permission(3, "", ""));
        permissions.add(new Permission(4, "", ""));
        permissions.add(new Permission(5, "", ""));
        addGroupIfMissing("staff", permissions);
        addGroupIfMissing("supervisor", permissions);
    }


    private void seedStatus() {
        addStatusIfMissing("online");
        addStatusIfMissing("away" );
        addStatusIfMissing("offline");
    }

    private void seedEmployee() {
        Employee emp = new Employee();
        UserGroup group1 = new UserGroup();
        UserGroup group2 = new UserGroup();
        group1.setGroupId(1);
        group2.setGroupId(2);
        Status s1 = new Status();
        Status s2 = new Status();
        s1.setId(3);
        s2.setId(3);
        StatusLog log1 = new StatusLog();
        StatusLog log2 = new StatusLog();
        log1.setEmployee(emp);
        log1.setStatus(s1);
        emp.getStatusLogs().add(log1);

        emp.setUsername("binhbk");
        emp.setUserGroup(group1);
        emp.setPassword(passwordEncoder.encode("Abcd@1234"));
        emp.setName("Bui Khac Binh");
        emp.setActive(true);

        Employee emp2 = new Employee();
        emp2.setUsername("admin");
        emp2.setUserGroup(group2);
        emp2.setPassword(passwordEncoder.encode("Abcd@1234"));
        emp2.setName("Admin");
        emp2.setActive(true);
        log2.setEmployee(emp2);
        log2.setStatus(s2);
        emp2.getStatusLogs().add(log2);

        addEmployeeIfMissing(emp);
        addEmployeeIfMissing(emp2);
    }


    private void seedProgressStatuses() {
        addProgressIfMissing("pending", "Chờ xử lý");
        addProgressIfMissing("onHold", "Tạm hoãn");
        addProgressIfMissing("resolved", "Đã xử lý");
    }

    private void seedCustomerEmotions() {
        addEmotionIfMissing("angry", "Tức giận");
        addEmotionIfMissing("negative", "Tiêu cực");
        addEmotionIfMissing("neutral", "Trung lập");
        addEmotionIfMissing("positive", "Tích cực");
        addEmotionIfMissing("happy", "Hài lòng");
    }

    private void seedCustomerSatisfactions() {
        addSatisfactionIfMissing(1, "VeryBad");
        addSatisfactionIfMissing(2, "Unhappy");
        addSatisfactionIfMissing(3, "Neutral");
        addSatisfactionIfMissing(4, "Pleased");
        addSatisfactionIfMissing(5, "Happy");
    }

    private void addProgressIfMissing(String code, String name) {
        if (!progressStatusRepository.existsByCode(code)) {
            progressStatusRepository.save(new ProgressStatus(0, code, name));
        }
    }

    private void addEmployeeIfMissing(Employee employee) {
        if (!employeeRepository.existsByUsername(employee.getUsername())) {
            employeeRepository.save(employee);
        }
    }

    private void addStatusIfMissing(String name) {
        if (!statusRepository.existsByName(name)) {
            statusRepository.save(new Status(0, name, null));
        }
    }


    private void addEmotionIfMissing(String code, String name) {
        if (!emotionRepository.existsByCode(code)) {
            emotionRepository.save(new Emotion(0, code, name));
        }
    }

    private void addSatisfactionIfMissing(int score, String comment) {
        if (!satisfactionRepository.existsByScore(score)) {
            satisfactionRepository.save(new Satisfaction(0, score, comment));
        }
    }

    private void addPermissionIfMissing(String permissiom) {
        if (!permissionRepository.existsByName(permissiom)) {
            permissionRepository.saveAndFlush(new Permission(0, permissiom, "test"));
        }
    }

    private void addGroupIfMissing(String name, Set<Permission> permissions) {
        if (!userGroupRepository.existsByName(name)) {
            userGroupRepository.saveAndFlush(new UserGroup(0, name, "test", null, permissions));
        }
    }
}
