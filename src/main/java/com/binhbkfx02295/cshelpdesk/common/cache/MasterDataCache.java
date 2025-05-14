package com.binhbkfx02295.cshelpdesk.common.cache;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Status;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.repository.EmployeeRepository;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.repository.StatusRepository;
import com.binhbkfx02295.cshelpdesk.employee_management.usergroup.UserGroup;
import com.binhbkfx02295.cshelpdesk.employee_management.usergroup.UserGroupRepository;
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
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.mapper.TicketMapper;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.repository.TicketRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.service.TicketServiceImpl;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
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
    private final TicketRepository ticketRepository;


    private Map<String, ProgressStatus> progressMap;
    private Map<String, Emotion> emotionMap;
    private Map<String, Satisfaction> satisfactionMap;
    private Map<String, Category> categoryMap;
    private Map<String, Status> statusMap;
    private Map<String, Employee> employeeMap;
    private Map<Integer, UserGroup> groupMap;
    private Map<String, Tag> tagMap;
    private Map<Integer,Ticket> openingTickets;

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

    public Map<Integer, Ticket> getALlTickets() {
        return openingTickets;
    }

    public Ticket getTicket(int id) {
        return openingTickets.getOrDefault(id, null);
    }

    public void updateALlTags() {
        this.tagMap = tagRepository.findAll().stream().collect(Collectors.toMap(Tag::getName, Function.identity()));
    }


    public ProgressStatus getProgress(String code) {
        return progressMap.getOrDefault(code, null);
    }

    public Emotion getEmotion(String code) {
        return emotionMap.getOrDefault(code, null);
    }

    public Satisfaction getSatisfaction(String code) {
        return satisfactionMap.getOrDefault(code, null);
    }

    public Category getCategory(String code) {
        return categoryMap.getOrDefault(code, null);
    }

    public UserGroup getUserGroup(int id) {
        return groupMap.getOrDefault(id, null);
    }

    public Status getStatus(String status) {
        return statusMap.getOrDefault(status, null);
    }

    public Employee getEmployee(String username) {
        return employeeMap.getOrDefault(username, null);
    }

    public Map<String, Employee> getAllEmployees() {
        return employeeMap;
    }

    public Map<String, ProgressStatus> getAllProgress() {
        return progressMap;
    }

    public Map<String, Emotion> getAllEmotions() {
        return emotionMap;
    }

    public Map<String, Satisfaction> getAllSatisfactions() {
        return satisfactionMap;
    }

    public Map<String, Category> getAllCategories() {
        return categoryMap;
    }

    public Map<Integer, UserGroup> getAllUserGroup() {
        return groupMap;
    }

    public Map<String, Status> getAllStatus() {
        return statusMap;
    }

    // update cache
    public void updateAllEmployees() {
        this.employeeMap = employeeRepository.findAll().stream()
                .collect(Collectors.toMap(Employee::getUsername, Function.identity()));
    }


    public void updateAllEmotions() {
        this.emotionMap = emotionRepository.findAll().stream()
                .collect(Collectors.toMap(Emotion::getCode, Function.identity()));
    }

    public void updateAllSatisfactions() {
        this.satisfactionMap = satisfactionRepository.findAll().stream()
                .collect(Collectors.toMap(Satisfaction::getCode, Function.identity()));
    }

    public void updateAllCategories() {
        this.categoryMap = categoryRepository.findAll().stream()
                .collect(Collectors.toMap(Category::getCode, Function.identity()));
    }

    public void updateAllProgressStatus() {
        this.progressMap = progressStatusRepository.findAll().stream()
                .collect(Collectors.toMap(ProgressStatus::getCode, Function.identity()));
    }

    public void updateAllOnlineStatus() {
        this.statusMap = statusRepository.findAll().stream()
                .collect(Collectors.toMap(Status::getName, Function.identity()));

    }

    private void updateAllGroups() {
        this.groupMap = groupRepository.findAll().stream()
                .collect(Collectors.toMap(UserGroup::getGroupId, Function.identity()));
    }

    public void updateEmployee(Employee user) {
        this.employeeMap.put(user.getUsername(), user);
    }

    public Tag getTag(String tag) {
        return tagMap.getOrDefault(tag, null);
    }

    public void addTicket(Ticket saved) {
        try {
            this.openingTickets.put(saved.getId(), saved);
        } catch (Exception e) {
            log.error(Arrays.toString(e.getStackTrace()));
        }
    }
}
