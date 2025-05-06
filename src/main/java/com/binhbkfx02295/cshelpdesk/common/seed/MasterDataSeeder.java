package com.binhbkfx02295.cshelpdesk.common.seed;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.EmployeeDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Status;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.repository.StatusRepository;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.service.EmployeeServiceImpl;
import com.binhbkfx02295.cshelpdesk.employee_management.permission.Permission;
import com.binhbkfx02295.cshelpdesk.employee_management.permission.PermissionRepository;
import com.binhbkfx02295.cshelpdesk.employee_management.usergroup.UserGroup;
import com.binhbkfx02295.cshelpdesk.employee_management.usergroup.UserGroupRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.entity.Emotion;
import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.Satisfaction;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.entity.ProgressStatus;
import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.repository.EmotionRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.SatisfactionRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.repository.ProgressStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class MasterDataSeeder implements CommandLineRunner {

    private final ProgressStatusRepository progressStatusRepository;
    private final EmotionRepository emotionRepository;
    private final SatisfactionRepository satisfactionRepository;
    private final EmployeeServiceImpl employeeService;
    private final StatusRepository  statusRepository;
    private final UserGroupRepository userGroupRepository;
    private final PermissionRepository permissionRepository;


    @Override
    public void run(String... args) {
        seedPermission();
        seedUserGroup();
        seedStatus();
        seedEmployee();
        seedProgressStatuses();
        seedCustomerEmotions();
        seedCustomerSatisfactions();
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
        EmployeeDTO emp = new EmployeeDTO();
        emp.setUsername("binhbk");
        emp.setGroupId(1);
        emp.setPassword("Abcd@1234");
        emp.setName("Bui Khac Binh");

        EmployeeDTO emp2 = new EmployeeDTO();
        emp2.setUsername("admin");
        emp2.setGroupId(2);
        emp2.setPassword("Abcd@1234");
        emp2.setName("Admin");

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

    private void addEmployeeIfMissing(EmployeeDTO employeeDTO) {
        if (employeeService.existsByUsername(employeeDTO.getUsername()).getHttpCode() != 200) {
            employeeService.createUser(employeeDTO);
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
