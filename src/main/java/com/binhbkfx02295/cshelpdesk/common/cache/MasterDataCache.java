package com.binhbkfx02295.cshelpdesk.common.cache;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Status;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.repository.EmployeeRepository;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.repository.StatusRepository;
import com.binhbkfx02295.cshelpdesk.employee_management.usergroup.UserGroup;
import com.binhbkfx02295.cshelpdesk.employee_management.usergroup.UserGroupRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.category.entity.Category;
import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.entity.Emotion;
import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.Satisfaction;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.entity.ProgressStatus;
import com.binhbkfx02295.cshelpdesk.ticket_management.category.repository.CategoryRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.repository.EmotionRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.SatisfactionRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.repository.ProgressStatusRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.tag.entity.Tag;
import com.binhbkfx02295.cshelpdesk.ticket_management.tag.repository.TagRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MasterDataCache {

    private final ProgressStatusRepository progressStatusRepository;
    private final EmotionRepository emotionRepository;
    private final SatisfactionRepository satisfactionRepository;
    private final CategoryRepository categoryRepository;
    private final StatusRepository statusRepository;
    private final EmployeeRepository employeeRepository;
    private final UserGroupRepository groupRepository;
    private final TagRepository tagRepository;

    private Map<String, ProgressStatus> progressMap;
    private Map<Integer, Emotion> emotionMap;
    private Map<Integer, Satisfaction> satisfactionMap;
    private Map<String, Category> categoryMap;
    private Map<String, Status> statusMap;
    private Map<String, Employee> employeeMap;
    private Map<Integer, UserGroup> groupMap;
    private Map<String, Tag> tagMap;

    @PostConstruct
    public void init() {
        updateAllProgressStatus();
        updateAllEmotions();
        updateAllSatisfactions();
        updateAllCategories();
        updateAllOnlineStatus();
        updateAllEmployees();
        updateAllGroups();
        updateALlTags();
    }

    private void updateALlTags() {
        this.tagMap = tagRepository.findAll().stream().collect(Collectors.toMap(Tag::getName, Function.identity()));
    }


    public ProgressStatus getProgress(String code) {
        return progressMap.getOrDefault(code, null);
    }

    public Emotion getEmotion(int id) {
        return emotionMap.getOrDefault(id, null);
    }

    public Satisfaction getSatisfaction(int score) {
        return satisfactionMap.getOrDefault(score, null);
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

    public Map<Integer, Emotion> getAllEmotions() {
        return emotionMap;
    }

    public Map<Integer, Satisfaction> getAllSatisfactions() {
        return satisfactionMap;
    }

    public Map<String, Category> getAllCategories() {
        return categoryMap;
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
                .collect(Collectors.toMap(Emotion::getId, Function.identity()));
    }

    public void updateAllSatisfactions() {
        this.satisfactionMap = satisfactionRepository.findAll().stream()
                .collect(Collectors.toMap(Satisfaction::getScore, Function.identity()));
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
}
