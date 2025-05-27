package com.binhbkfx02295.cshelpdesk.infrastructure.common.cache;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Status;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.repository.EmployeeRepository;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.repository.StatusRepository;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.service.EmployeeServiceImpl;
import com.binhbkfx02295.cshelpdesk.employee_management.usergroup.UserGroup;
import com.binhbkfx02295.cshelpdesk.employee_management.usergroup.UserGroupRepository;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.message.entity.Message;
import com.binhbkfx02295.cshelpdesk.message.repository.MessageRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.category.entity.Category;
import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.entity.Emotion;
import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.entity.Satisfaction;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.entity.ProgressStatus;
import com.binhbkfx02295.cshelpdesk.ticket_management.category.repository.CategoryRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.repository.EmotionRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.repository.SatisfactionRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.repository.ProgressStatusRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.tag.entity.Tag;
import com.binhbkfx02295.cshelpdesk.ticket_management.tag.repository.TagRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDashboardDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.repository.TicketRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.service.TicketServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.apache.bcel.util.ClassLoaderRepository;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j

public class MasterDataCache {

    private final ProgressStatusRepository progressStatusRepository;
    private final EmotionRepository emotionRepository;
    private final SatisfactionRepository satisfactionRepository;
    private final CategoryRepository categoryRepository;
    private final StatusRepository statusRepository;
    private final EmployeeRepository employeeRepository;
    private final UserGroupRepository groupRepository;
    private final TagRepository tagRepository;
    private final MessageRepository messageRepository;
    private final TicketRepository ticketRepository;


    private Map<Integer, ProgressStatus> progressMap = new HashMap<>();
    private Map<Integer, Emotion> emotionMap  = new HashMap<>();
    private Map<Integer, Satisfaction> satisfactionMap = new HashMap<>();
    private Map<Integer, Category> categoryMap = new HashMap<>();
    private Map<Integer, Status> statusMap = new HashMap<>();
    private Map<String, Employee> employeeMap = new HashMap<>();
    private Map<Integer, UserGroup> groupMap = new HashMap<>();
    private Map<Integer, Tag> tagMap = new HashMap<>();
    private Map<Integer, Ticket> openingTickets = new HashMap<>();
    private Map<Integer, Message> messages = new HashMap<>();

    public void refresh() {
        updateAllProgressStatus();
        updateAllEmotions();
        updateAllSatisfactions();
        updateAllCategories();
        updateAllOnlineStatus();
        updateAllEmployees();
        updateAllGroups();
        updateALlTags();
        updateOpeningTickets();
        updateAllMessages();
        log.info("caching successfully");
    }

    public void updateOpeningTickets() {
        try {
            LocalDate today = LocalDate.now();
            LocalDateTime startDateTime = today.atStartOfDay();
            LocalDateTime endDateTime = today.plusDays(1).atStartOfDay().minusNanos(1);
            Timestamp startOfDay = Timestamp.valueOf(startDateTime);
            Timestamp endOfDay = Timestamp.valueOf(endDateTime);
            List<Ticket> result = ticketRepository.findOpeningOrToday(startOfDay, endOfDay);
            openingTickets = result.stream().collect(Collectors.toMap(Ticket::getId, Function.identity()));
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public Map<Integer, Ticket> getDashboardTickets() {
        return openingTickets;
    }

    public Ticket getTicket(int id) {
        return openingTickets.getOrDefault(id, null);
    }

    public void updateALlTags() {
        this.tagMap = tagRepository.findAll().stream().collect(Collectors.toMap(Tag::getId, Function.identity()));
    }


    public ProgressStatus getProgress(int id) {
        return progressMap.getOrDefault(id, null);
    }

    public Emotion getEmotion(int id) {
        return emotionMap.getOrDefault(id, null);
    }

    public Satisfaction getSatisfaction(int id) {
        return satisfactionMap.getOrDefault(id, null);
    }

    public Category getCategory(int id) {
        return categoryMap.getOrDefault(id, null);
    }

    public UserGroup getUserGroup(int id) {
        return groupMap.getOrDefault(id, null);
    }

    public Status getStatus(int id) {
        return statusMap.getOrDefault(id, null);
    }

    public Employee getEmployee(String username) {
        return employeeMap.getOrDefault(username, null);
    }

    public Map<String, Employee> getAllEmployees() {
        return employeeMap;
    }

    public Map<Integer, ProgressStatus> getAllProgress() {
        return progressMap;
    }

    public Map<Integer, Emotion> getAllEmotions() {
        return emotionMap;
    }

    public Map<Integer, Satisfaction> getAllSatisfactions() {
        return satisfactionMap;
    }

    public Map<Integer, Category> getAllCategories() {
        return categoryMap;
    }

    public Map<Integer, UserGroup> getAllUserGroup() {
        return groupMap;
    }

    public Map<Integer, Status> getAllStatus() {
        return statusMap;
    }

    public Map<Integer, Tag> getAllTag() {
        return tagMap;
    }

    // update cache
    public void updateAllEmployees() {
        List<Employee> employeeList = employeeRepository.findAllWithTop1EmployeeStatusLog();
        this.employeeMap = employeeList.stream()
                .collect(Collectors.toMap(Employee::getUsername, Function.identity()));
    }

    public void updateAllEmotions() {
        this.emotionMap = emotionRepository.findAll().stream()
                .collect(Collectors.toMap(Emotion::getId, Function.identity()));
    }

    public void updateAllSatisfactions() {
        this.satisfactionMap = satisfactionRepository.findAll().stream()
                .collect(Collectors.toMap(Satisfaction::getId, Function.identity()));
    }

    public void updateAllCategories() {
        this.categoryMap = categoryRepository.findAll().stream()
                .collect(Collectors.toMap(Category::getId, Function.identity()));
    }

    public void updateAllProgressStatus() {
        this.progressMap = progressStatusRepository.findAll().stream()
                .collect(Collectors.toMap(ProgressStatus::getId, Function.identity()));
    }

    public void updateAllOnlineStatus() {
        this.statusMap = statusRepository.findAll().stream()
                .collect(Collectors.toMap(Status::getId, Function.identity()));
    }
    private void updateAllGroups() {
        this.groupMap = groupRepository.findAll().stream()
                .collect(Collectors.toMap(UserGroup::getGroupId, Function.identity()));
    }

    public void updateEmployee(Employee employee) {
        this.employeeMap.put(employee.getUsername(), employee);
    }

    public Tag getTag(int id) {
        return tagMap.getOrDefault(id, null);
    }

    public void putTicket(Ticket saved) {
        try {
            this.openingTickets.put(saved.getId(), saved);
        } catch (Exception e) {
            log.error(Arrays.toString(e.getStackTrace()));
        }
    }

    public void putMessage(Message saved) {
        try {
            this.messages.put(saved.getId(), saved);
        } catch (Exception e) {
            log.error(Arrays.toString(e.getStackTrace()));
        }
    }

    public Map<Integer, Message> getAllMessages() {
        return this.messages;
    }

    public void updateAllMessages() {
        List<Ticket> openingTickets = this.openingTickets.values().stream().toList();
        this.messages = new HashMap<>();
        for (Ticket ticket: openingTickets) {
            List<Message> messages= messageRepository.findByTicket_Id(ticket.getId());
            messages.forEach(message -> this.messages.put(message.getId(), message));
        }
    }
}