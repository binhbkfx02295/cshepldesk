package com.binhbkfx02295.cshelpdesk.infrastructure.common.seed;

import com.binhbkfx02295.cshelpdesk.infrastructure.common.cache.MasterDataCache;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Status;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.StatusLog;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.repository.EmployeeRepository;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.repository.StatusRepository;
import com.binhbkfx02295.cshelpdesk.employee_management.permission.Permission;
import com.binhbkfx02295.cshelpdesk.employee_management.permission.PermissionRepository;
import com.binhbkfx02295.cshelpdesk.employee_management.usergroup.UserGroup;
import com.binhbkfx02295.cshelpdesk.employee_management.usergroup.UserGroupRepository;
import com.binhbkfx02295.cshelpdesk.facebookuser.entity.FacebookUser;
import com.binhbkfx02295.cshelpdesk.facebookuser.repository.FacebookUserRepository;
import com.binhbkfx02295.cshelpdesk.message.entity.Message;
import com.binhbkfx02295.cshelpdesk.message.repository.MessageRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.category.entity.Category;
import com.binhbkfx02295.cshelpdesk.ticket_management.category.repository.CategoryRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.entity.Emotion;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.model.Criteria;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.repository.CriteriaRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.entity.Satisfaction;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.entity.ProgressStatus;
import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.repository.EmotionRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.repository.SatisfactionRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.repository.ProgressStatusRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.*;

import com.github.javafaker.Faker;
import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class MasterDataSeeder implements CommandLineRunner {

    private final ProgressStatusRepository progressStatusRepository;
    private final EmotionRepository emotionRepository;
    private final SatisfactionRepository satisfactionRepository;
    private final StatusRepository  statusRepository;
    private final UserGroupRepository userGroupRepository;
    private final PermissionRepository permissionRepository;
    private final EmployeeRepository employeeRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final MasterDataCache cache;
    private final FacebookUserRepository facebookUserRepository;
    private final TicketRepository ticketRepository;
    private final MessageRepository messageRepository;
    private final CriteriaRepository criteriaRepository;



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
//        seedFacebookUsers(50);
//        seedTickets(6720);
//        seedConversations(20);
        seedTicketCriterias();
        log.info("seeding done");
        cache.refresh();
    }

    private void seedTicketCriterias() {
        if (!criteriaRepository.findAll().isEmpty()) {
            return;
        }
        List<Criteria> criterias = new ArrayList<>();

        Criteria c1 = new Criteria();
        c1.setCode("salutation");
        c1.setName("Lời chào");
        c1.setDescription("Câu chào đón không thể hiện hiếu khách");
        c1.setActive(true);
        criterias.add(c1);

        Criteria c2 = new Criteria();
        c2.setCode("salutation");
        c2.setName("Lời chào");
        c2.setDescription("Không chào khách");
        c2.setActive(true);
        criterias.add(c2);

        Criteria c3 = new Criteria();
        c3.setCode("issue");
        c3.setName("Xác minh vấn đề");
        c3.setDescription("Mất nhiều thời gian hơn thông thường để xác minh");
        c3.setActive(true);
        criterias.add(c3);

        Criteria c4 = new Criteria();
        c4.setCode("issue");
        c4.setName("Xác minh vấn đề");
        c4.setDescription("Khiến khách khó chịu vì không hiểu vấn đề");
        c4.setActive(true);
        criterias.add(c4);

        Criteria c5 = new Criteria();
        c5.setCode("onhold");
        c5.setName("Đặt chờ đợi");
        c5.setDescription("Lặp lại việc đặt đợi hời hợt mà không có sự xoa dịu hay leo thang phù hợp");
        c5.setActive(true);
        criterias.add(c5);

        Criteria c6 = new Criteria();
        c6.setCode("onhold");
        c6.setName("Đặt chờ đợi");
        c6.setDescription("Đợi quá 3 lần mà không hẹn chủ động liên hệ");
        c6.setActive(true);
        criterias.add(c6);

        Criteria c7 = new Criteria();
        c7.setCode("resolution");
        c7.setName("Xử lý vấn đề");
        c7.setDescription("Mất nhiều thời gian/nỗ lực hơn thông thường");
        c7.setActive(true);
        criterias.add(c7);

        Criteria c8 = new Criteria();
        c8.setCode("resolution");
        c8.setName("Xử lý vấn đề");
        c8.setDescription("Thất bại giải quyết vấn đề của khách");
        c8.setActive(true);
        criterias.add(c8);

        Criteria c9 = new Criteria();
        c9.setCode("closure");
        c9.setName("Kết thúc");
        c9.setDescription("Kết thúc mà chưa có câu xác nhận với khách");
        c9.setActive(true);
        criterias.add(c9);

        Criteria c10 = new Criteria();
        c10.setCode("closure");
        c10.setName("Kết thúc");
        c10.setDescription("Tắt ngang hoặc ép buộc kết thúc");
        c10.setActive(true);
        criterias.add(c10);

        Criteria c11 = new Criteria();
        c11.setCode("formality");
        c11.setName("Hình thức");
        c11.setDescription("Xưng hô không phù hợp, chỉ chấp nhận (Anh/chi/quý khách với em)");
        c11.setActive(true);
        criterias.add(c11);

        Criteria c12 = new Criteria();
        c12.setCode("content");
        c12.setName("Nội dung");
        c12.setDescription("Dùng từ viết tắt, từ lóng, địa phương");
        c12.setActive(true);
        criterias.add(c12);

        Criteria c13 = new Criteria();
        c13.setCode("content");
        c13.setName("Nội dung");
        c13.setDescription("Dùng từ khó hiểu, gây hiểu lầm");
        c13.setActive(true);
        criterias.add(c13);

        Criteria c14 = new Criteria();
        c14.setCode("frequency");
        c14.setName("Tần suất");
        c14.setDescription("Ngắn cục không đủ thông tin khiến khách phải hỏi lại");
        c14.setActive(true);
        criterias.add(c14);

        Criteria c15 = new Criteria();
        c15.setCode("frequency");
        c15.setName("Tần suất");
        c15.setDescription("Dài lê thê khó đọc");
        c15.setActive(true);
        criterias.add(c15);

        Criteria c16 = new Criteria();
        c16.setCode("enthusiasm");
        c16.setName("Năng lượng");
        c16.setDescription("Sử dụng ngôn từ không phù hợp, phân biệt, hay ép buộc áp đặt và kết thúc ngang cuộc trò chuyện");
        c16.setActive(true);
        criterias.add(c16);

        Criteria c17 = new Criteria();
        c17.setCode("enthusiasm");
        c17.setName("Năng lượng");
        c17.setDescription("Đổ lỗi hay đối kháng chất vấn ngược");
        c17.setActive(true);
        criterias.add(c17);

        Criteria c18 = new Criteria();
        c18.setCode("enthusiasm");
        c18.setName("Năng lượng");
        c18.setDescription("Chỉ ở mức độ hỏi gì đáp nấy và không né tránh vấn đề");
        c18.setActive(true);
        criterias.add(c18);

        Criteria c19 = new Criteria();
        c19.setCode("addon");
        c19.setName("Cung cấp thêm thông tin");
        c19.setDescription("Không cung cấp thêm về products, promotions...");
        c19.setActive(true);
        criterias.add(c19);

        Criteria c20 = new Criteria();
        c20.setCode("addon");
        c20.setName("Cung cấp thêm thông tin");
        c20.setDescription("Cung cấp không đúng thời điểm: sau khi giải quyết xong vấn đề hoặc lúc đang đặt chờ chờ đợi.");
        c20.setActive(true);
        criterias.add(c20);

        Criteria c21 = new Criteria();
        c21.setCode("addon");
        c21.setName("Cung cấp thêm thông tin");
        c21.setDescription("Cung cấp khi khách đang thái độ tiêu cực");
        c21.setActive(true);
        criterias.add(c21);

        criteriaRepository.saveAll(criterias);
    }


    private void seedConversations(int num) {
        Random random = new Random();

        List<Ticket> tickets = ticketRepository.findAll();

        tickets.forEach((ticket) -> {
            List<Message> result = messageRepository.findByTicket_Id(ticket.getId());
            if (!result.isEmpty()) {
                log.info("seed message cho ticket #{}: ticket da co message", ticket.getId());
                return;
            }
            List<Message> temp = new ArrayList<>();
            Timestamp baseTime = ticket.getCreatedAt();
            long currentTime = baseTime.getTime();
            for (int i=0; i<num; i++) {
                Message msg = new Message();
                msg.setTicket(ticket);
                msg.setSenderEmployee(random.nextBoolean());
                msg.setText("Tin nhắn #" + (i + 1) + " cho ticket #" + ticket.getId());
                int interval = 6 + random.nextInt(115); // 6..120
                currentTime += interval * 1000L;
                msg.setTimestamp(new Timestamp(currentTime));
//                messageRepository.save(msg);
                temp.add(msg);
            }
            messageRepository.saveAll(temp);
            log.info("seed message cho ticket #{}: thanh cong", ticket.getId());
        });

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

        Category category6 = new Category();
        category6.setCode("other");
        category6.setName("Khác");
        addCategoryIfMissing(category6);
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
        addGroupIfMissing("Nhân Viên", "staff", permissions);
        addGroupIfMissing("Quản Lý", "supervisor", permissions);
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

    private void addGroupIfMissing(String name, String code, Set<Permission> permissions) {
        if (!userGroupRepository.existsByName(name)) {
            userGroupRepository.saveAndFlush(new UserGroup(0, name, code, "test", null, permissions));
        }
    }

    public void seedFacebookUsers(int num) {
        List<FacebookUser> list = facebookUserRepository.getAll();
        if (list.size() >= num) {
            return;
        }
        Faker faker = new Faker(new Locale("vi")); // Vietnamese locale
        Random random = new Random();
        for (int i = list.size();i  < num; i++) {
            FacebookUser user = new FacebookUser();

            String facebookId = String.format(String.format("1234567890123%d", i));
            String realName = faker.name().fullName(); // e.g. "Khắc Bình"
            String facebookName = "FbUser " + faker.name().lastName(); // e.g. "FbUser Bùi"
            String email = faker.internet().emailAddress();
            String phone = String.format("0%09d", random.nextInt(1_0000_0000)); // e.g. 0987654321
            String zalo = String.format("0%09d", random.nextInt(1_0000_0000));
            Instant createdAt = faker.date()
                    .between(Date.from(Instant.now().minusSeconds(3600 * 24 * 365)), Date.from(Instant.now()))
                    .toInstant();

            user.setFacebookId(facebookId);
            user.setFacebookName(facebookName);
            user.setRealName(realName);
            user.setEmail(email);
            user.setPhone(phone);
            user.setZalo(zalo);
            user.setCreatedAt(createdAt);
            user.setFacebookProfilePic(String.format("img/placeholder-facebook-%d.jpg",  (i % 5)+1 ));


            boolean result = facebookUserRepository.existsById(user.getFacebookId());
            log.info("seeding user {}", result);
            if (!result) {
                facebookUserRepository.save(user);
            }
        }
    }

    public void seedTickets(int num) {
        List<Ticket> ticketList = ticketRepository.findAll();
        if (ticketList.size() >= num) {
            return;
        }
        List<FacebookUser> facebookUsers = facebookUserRepository.getAll();
        List<Employee> employeeList = cache.getAllMessages().isEmpty() ? employeeRepository.findAll() :
                cache.getAllEmployees().values().stream().toList();
        List<Category> categoryList = cache.getAllCategories().isEmpty() ? categoryRepository.findAll() :
                cache.getAllCategories().values().stream().toList();
        List<ProgressStatus> progressStatusList = cache.getAllProgress().isEmpty() ? progressStatusRepository.findAll() :
                cache.getAllProgress().values().stream().toList();
        log.info("employee List: {}", employeeList.size());
        List<Ticket> temp = new ArrayList<>();
        for (int i = 0; i < num - ticketList.size(); i++) {
            Faker faker = new Faker(new Locale("vi")); // Vietnamese locale
            Employee employee = new Employee();
            employee.setUsername(employeeList.get(i % 2).getUsername());
            Ticket ticket = new Ticket();
            ticket.setTitle(null); // random string
            ticket.setAssignee(employee); //get random from employeeList
            ticket.setFacebookUser(facebookUsers.get( i % facebookUsers.size()));
            ticket.setCategory(categoryList.get(i % 5));
            ticket.setProgressStatus(progressStatusList.get(i % 3));

            Instant createdAt = faker.date()
                    .between(Date.from(Instant.now().minusSeconds(3600 * 24 * 30)), Date.from(Instant.now()))
                    .toInstant();

            ticket.setCreatedAt(Timestamp.from(createdAt));
            temp.add(ticket);
        }
        ticketRepository.saveAll(temp);
    }


}
