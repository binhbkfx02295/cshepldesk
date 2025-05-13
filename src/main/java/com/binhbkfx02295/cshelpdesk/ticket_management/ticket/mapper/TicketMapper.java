package com.binhbkfx02295.cshelpdesk.ticket_management.ticket.mapper;

import com.binhbkfx02295.cshelpdesk.common.cache.MasterDataCache;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.EmployeeDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.mapper.EmployeeMapper;
import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.entity.FacebookUser;
import com.binhbkfx02295.cshelpdesk.facebookuser.mapper.FacebookUserMapper;
import com.binhbkfx02295.cshelpdesk.ticket_management.category.dto.CategoryDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.category.entity.Category;
import com.binhbkfx02295.cshelpdesk.ticket_management.category.mapper.CategoryMapper;
import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.dto.EmotionDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.entity.Emotion;
import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.mapper.EmotionMapper;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.dto.ProgressStatusDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.entity.ProgressStatus;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.mapper.ProgressStatusMapper;
import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.entity.Satisfaction;
import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.mapper.SatisfactionMapper;
import com.binhbkfx02295.cshelpdesk.ticket_management.tag.dto.TagDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.tag.mapper.TagMapper;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDashboardDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDetailDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import com.binhbkfx02295.cshelpdesk.ticket_management.tag.entity.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TicketMapper {

    private final MasterDataCache cache;
    private final ProgressStatusMapper progressStatusMapper;
    private final EmployeeMapper employeeMapper;
    private final FacebookUserMapper facebookUserMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final EmotionMapper emotionMapper;
    private final SatisfactionMapper satisfactionMapper;

    public TicketDTO toListDTO(Ticket ticket) {
        if (ticket == null) return null;
        TicketDTO dto = new TicketDTO();

        dto.setId(ticket.getId());
        dto.setTitle(ticket.getTitle());
        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setUpdatedAt(ticket.getLastUpdateAt());
        dto.setClosedAt(ticket.getClosedAt());

        dto.setAssignee(ticket.getAssignee() != null ? employeeMapper.toDTO(ticket.getAssignee()) : null);
        dto.setFacebookUser(ticket.getFacebookUser() != null ? facebookUserMapper.toListDTO(ticket.getFacebookUser()) : null);
        dto.setProgressStatus(progressStatusMapper.toDTO(ticket.getProgressStatus()));
        dto.setCategory(ticket.getCategory() != null ? categoryMapper.toDTO(ticket.getCategory()) : null);

        return dto;
    }

    public TicketDetailDTO toDetailDTO(Ticket ticket) {
        if (ticket == null) return null;

        TicketDetailDTO dto = new TicketDetailDTO();

        dto.setId(ticket.getId());
        dto.setTitle(ticket.getTitle());
        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setUpdatedAt(ticket.getLastUpdateAt());
        dto.setClosedAt(ticket.getClosedAt());

        dto.setAssignee(ticket.getAssignee() != null ? employeeMapper.toDTO(ticket.getAssignee()) : null);
        dto.setFacebookUser(ticket.getFacebookUser() != null ? facebookUserMapper.toListDTO(ticket.getFacebookUser()) : null);
        dto.setProgressStatus(progressStatusMapper.toDTO(ticket.getProgressStatus()));
        dto.setCategory(ticket.getCategory() != null ? categoryMapper.toDTO(ticket.getCategory()) : null);
        dto.setEmotion(ticket.getEmotion() != null ? emotionMapper.toDTO(ticket.getEmotion()) : null);
        dto.setSatisfaction(ticket.getSatisfaction() != null ? satisfactionMapper.toDTO(ticket.getSatisfaction()) : null);

        dto.setTags(ticket.getTags() != null ?
                ticket.getTags().stream().map(tagMapper::toDTO).collect(Collectors.toList()) :
                Collections.emptyList());

        return dto;
    }

    public Ticket toEntity(TicketDetailDTO dto) {
        if (dto == null) return null;
        Ticket ticket = new Ticket();

        ticket.setTitle(dto.getTitle());

        ticket.setAssignee(dto.getAssignee() != null ? cache.getEmployee(dto.getAssignee().getUsername()) : null);
        ticket.setProgressStatus(cache.getProgress(dto.getProgressStatus().getCode()));
        ticket.setCategory(dto.getCategory() != null ? cache.getCategory(dto.getCategory().getCode()) : null);
        ticket.setEmotion(dto.getEmotion() != null ? cache.getEmotion(dto.getEmotion().getCode()) : null);
        ticket.setSatisfaction(dto.getSatisfaction() != null ? cache.getSatisfaction(dto.getSatisfaction().getCode()) : null);

        if (dto.getFacebookUser() != null) {
            FacebookUser facebookUser = new FacebookUser();
            facebookUser.setFacebookId(dto.getFacebookUser().getFacebookId());
            ticket.setFacebookUser(facebookUser);
        }

        return ticket;
    }

    public Ticket toDashboard(TicketDashboardDTO dto) {
        if (dto == null) return null;
        Ticket ticket = new Ticket();

        ticket.setTitle(dto.getTitle());

        ticket.setAssignee(dto.getAssignee() != null ? cache.getEmployee(dto.getAssignee().getUsername()) : null);
        ticket.setProgressStatus(cache.getProgress(dto.getProgressStatus().getCode()));
        if (dto.getFacebookUser() != null) {
            FacebookUser facebookUser = new FacebookUser();
            facebookUser.setFacebookId(dto.getFacebookUser().getFacebookId());
            ticket.setFacebookUser(facebookUser);
        }

        return ticket;
    }

    public Ticket mergeToEntity(TicketDetailDTO dto, Ticket entity) {
        if (dto == null || entity == null) return entity;

        // 1. Title
        if (dto.getTitle() != null && !dto.getTitle().equals(entity.getTitle())) {
            entity.setTitle(dto.getTitle());
        }

        // 3. Assignee (username)
        if (dto.getAssignee() != null && (entity.getAssignee() == null ||
                !dto.getAssignee().equals(entity.getAssignee().getUsername()))) {
            Employee assignee = cache.getEmployee(dto.getAssignee().getUsername());
            entity.setAssignee(assignee);
        }

        // 4. ProgressStatus (name hoặc code)
        if (!dto.getProgressStatus().equals(entity.getProgressStatus().getCode())) {
            ProgressStatus status = cache.getProgress(dto.getProgressStatus().getCode());
            entity.setProgressStatus(status);
        }

        // 5. Category (name)
        if (dto.getCategory() != null && (entity.getCategory() == null ||
                !dto.getCategory().equals(entity.getCategory().getName()))) {
            Category category = cache.getCategory(dto.getCategory().getCode());
            entity.setCategory(category);
        }

        // 6. Emotion (by ID)
        if (dto.getEmotion() != null && (entity.getEmotion() == null ||
                dto.getEmotion().getId() != entity.getEmotion().getId())) {
            Emotion emotion = cache.getEmotion(dto.getEmotion().getCode());
            entity.setEmotion(emotion);
        }

        // 7. Satisfaction (by score)
        if (dto.getSatisfaction() != null && (entity.getSatisfaction() == null ||
                dto.getSatisfaction().getId() != entity.getSatisfaction().getId())) {
            Satisfaction satisfaction = cache.getSatisfaction(dto.getSatisfaction().getCode());
            entity.setSatisfaction(satisfaction);
        }

        // 8. FacebookUser (by facebookId)
        if (dto.getFacebookUser() != null && (entity.getFacebookUser() == null ||
                !dto.getFacebookUser().getFacebookId().equals(entity.getFacebookUser().getFacebookId()))) {
            FacebookUser fb = new FacebookUser();
            fb.setFacebookId(dto.getFacebookUser().getFacebookId());
            entity.setFacebookUser(fb);
        }

        return entity;
    }

    public TicketDashboardDTO toDashboardDTO(Ticket entity) {
        TicketDashboardDTO dto = new TicketDashboardDTO();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setAssignee(entity.getAssignee() != null ? employeeMapper.toDTO(entity.getAssignee()) : null);
        dto.setFacebookUser(entity.getFacebookUser() != null ? facebookUserMapper.toListDTO(entity.getFacebookUser()) : null);
        dto.setProgressStatus(progressStatusMapper.toDTO(entity.getProgressStatus()));
        dto.setTitle(entity.getTitle());
        return dto;
    }
}
