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
import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.entity.FacebookUser;
import com.binhbkfx02295.cshelpdesk.facebookuser.repository.FacebookUserRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.category.entity.Category;
import com.binhbkfx02295.cshelpdesk.ticket_management.category.repository.CategoryRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.entity.Emotion;
import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.entity.Satisfaction;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.entity.ProgressStatus;
import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.repository.EmotionRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.repository.SatisfactionRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.repository.ProgressStatusRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ArrayList;
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
    private final CategoryRepository categoryRepository;
    private final MasterDataCache cache;
    private final FacebookUserRepository facebookUserRepository;


    @Override
    public void run(String... args) {
        seedPermission();
        seedUserGroup();
        seedStatus();
        seedEmployee();
        seedProgressStatuses();
        seedCustomerEmotions();
        seedCustomerSatisfactions();
        seedCategory();
        seedFacebookUsers();
        seedTickets();

        log.info("seeding done");
        cache.refresh();
    }

    private void seedTickets() {
    }

    private void seedFacebookUsers() {
        FacebookUser user = new FacebookUser();
        user.setFacebookId("9788775675676567");
        user.setFacebookFirstName("Minh Duc");
        user.setFacebookLastName("Le");
        user.setFacebookProfilePic("/img/placeholder-facebook-1.jpg");
        if (!facebookUserRepository.existsById("9788775675676567")) {
            facebookUserRepository.save(user);
        }

        FacebookUser u = new FacebookUser();
        u.setFacebookId("9788775675676565");
        u.setFacebookFirstName("Duc Vy");
        u.setFacebookLastName("Le Minh");
        u.setFacebookProfilePic("/img/placeholder-facebook-2.jpg");
        if (!facebookUserRepository.existsById("9788775675676565")) {
            facebookUserRepository.save(u);
        }

        FacebookUser u3 = new FacebookUser();
        u3.setFacebookId("9788775642676567");
        u3.setFacebookFirstName("Hoa Dong");
        u3.setFacebookLastName("Tan");
        u3.setFacebookProfilePic("/img/placeholder-facebook-3.jpg");
        if (!facebookUserRepository.existsById("9788775642676567")) {
            facebookUserRepository.save(u3);
        }

        FacebookUser u4 = new FacebookUser();
        u4.setFacebookId("9788775615676567");
        u4.setFacebookFirstName("Elise");
        u4.setFacebookLastName("Nguyen");
        u4.setFacebookProfilePic("/img/placeholder-facebook-4.jpg");
        if (!facebookUserRepository.existsById("9788775615676567")) {
            facebookUserRepository.save(u4);
        }

        FacebookUser u5 = new FacebookUser();
        u5.setFacebookId("9788555675676567");
        u5.setFacebookFirstName("Tran Nam");
        u5.setFacebookLastName("Dinh");
        u5.setFacebookProfilePic("/img/placeholder-facebook-5.jpg");
        if (!facebookUserRepository.existsById("9788555675676567")) {
            facebookUserRepository.save(u5);
        }
    }

    private void seedCategory() {
        Category category1 = new Category();
        category1.setCode("purchase");
        category1.setName("Mua Hàng");
        addCategoryIfMissing(category1);

        Category category2 = new Category();
        category2.setCode("complaint");
        category2.setName("Khiếu Nại");
        addCategoryIfMissing(category2);

        Category category3 = new Category();
        category3.setCode("payment");
        category3.setName("Thanh Toán");
        addCategoryIfMissing(category3);

        Category category4 = new Category();
        category4.setCode("promotion");
        category4.setName("Khuyến Mãi");
        addCategoryIfMissing(category4);

        Category category5 = new Category();
        category5.setCode("refund");
        category5.setName("Hoàn Tiền");
        addCategoryIfMissing(category5);
    }

    private void addCategoryIfMissing(Category category) {
        if (!categoryRepository.existsByCode(category.getCode())) {
            categoryRepository.save(category);
        }
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
        addProgressIfMissing("pending", "Đang xử lý");
        addProgressIfMissing("on-hold", "Đang chờ");
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
        addSatisfactionIfMissing("verybad", "Rất Tệ");
        addSatisfactionIfMissing("unhappy", "Không Vui");
        addSatisfactionIfMissing("neutral", "Trung Lập");
        addSatisfactionIfMissing("pleased", "Tạm Được");
        addSatisfactionIfMissing("happy", "Rất Vui");
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

    private void addSatisfactionIfMissing(String code, String name) {
        if (!satisfactionRepository.existsByCode(code)) {
            satisfactionRepository.save(new Satisfaction(0, code, name));
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
