package com.binhbkfx02295.cshelpdesk.ticket_management.ticket.mapper;

import com.binhbkfx02295.cshelpdesk.common.cache.MasterDataCache;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee;
import com.binhbkfx02295.cshelpdesk.facebookuser.entity.FacebookUser;
import com.binhbkfx02295.cshelpdesk.ticket_management.category.entity.Category;
import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.entity.Emotion;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.entity.ProgressStatus;
import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.entity.Satisfaction;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDTO;
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
    public TicketDTO toListDTO(Ticket ticket) {
        if (ticket == null) return null;

        return new TicketDTO(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getCreatedAt(),
                ticket.getAssignee() != null ? ticket.getAssignee().getUsername() : null,
                ticket.getProgressStatus() != null ? ticket.getProgressStatus().getCode() : null
        );
    }

    public TicketDetailDTO toDetailDTO(Ticket ticket) {
        if (ticket == null) return null;

        List<String> tagNames = ticket.getTags() != null ?
                ticket.getTags().stream().map(Tag::getName).collect(Collectors.toList()) :
                Collections.emptyList();

        return new TicketDetailDTO(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getCreatedAt(),
                ticket.getLastUpdateAt(),
                ticket.getClosedAt(),
                ticket.getAssignee() != null ? ticket.getAssignee().getUsername() : null,
                ticket.getProgressStatus() != null ? ticket.getProgressStatus().getName() : null,
                ticket.getCategory() != null ? ticket.getCategory().getName() : null,
                ticket.getEmotion() != null ? ticket.getEmotion().getId() : 0,
                ticket.getSatisfaction() != null ? ticket.getSatisfaction().getScore() : 0,
                ticket.getFacebookUser() != null ? ticket.getFacebookUser().getFacebookId() : null,
                tagNames
        );
    }

    public Ticket toEntity(TicketDetailDTO dto) {
        if (dto == null) return null;
        Ticket ticket = new Ticket();

        ticket.setTitle(dto.getTitle());

        if (dto.getProgressStatus() != null) {
            ProgressStatus status = cache.getProgress(dto.getProgressStatus());
            ticket.setProgressStatus(status);
        }

        if (dto.getAssignee() != null) {
            Employee assignee = new Employee();
            assignee.setUsername(dto.getAssignee());
            ticket.setAssignee(assignee);
        }

        if (dto.getFacebookUser() != null) {
            FacebookUser facebookUser = new FacebookUser();
            facebookUser.setFacebookId(dto.getFacebookUser());
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

        // 2. ClosedAt
        if (dto.getClosedAt() != null && !dto.getClosedAt().equals(entity.getClosedAt())) {
            entity.setClosedAt(dto.getClosedAt());
        } else if (dto.getClosedAt() == null && entity.getClosedAt() != null) {
            entity.setClosedAt(null);
        }

        // 3. Assignee (username)
        if (dto.getAssignee() != null && (entity.getAssignee() == null ||
                !dto.getAssignee().equals(entity.getAssignee().getUsername()))) {
            Employee assignee = cache.getEmployee(dto.getAssignee());
            entity.setAssignee(assignee);
        }

        // 4. ProgressStatus (name hoặc code)
        if (dto.getProgressStatus() != null && (entity.getProgressStatus() == null ||
                !dto.getProgressStatus().equals(entity.getProgressStatus().getCode()))) {
            ProgressStatus status = cache.getProgress(dto.getProgressStatus());
            entity.setProgressStatus(status);
        }

        // 5. Category (name)
        if (dto.getCategory() != null && (entity.getCategory() == null ||
                !dto.getCategory().equals(entity.getCategory().getName()))) {
            Category category = cache.getCategory(dto.getCategory());
            entity.setCategory(category);
        }

        // 6. Emotion (by ID)
        if (dto.getEmotion() > 0 && (entity.getEmotion() == null ||
                dto.getEmotion() != entity.getEmotion().getId())) {
            Emotion emotion = cache.getEmotion(dto.getEmotion());
            entity.setEmotion(emotion);
        }

        // 7. Satisfaction (by score)
        if (dto.getSatisfaction() > 0 && (entity.getSatisfaction() == null ||
                dto.getSatisfaction() != entity.getSatisfaction().getScore())) {
            Satisfaction satisfaction = cache.getSatisfaction(dto.getSatisfaction());
            entity.setSatisfaction(satisfaction);
        }

        // 8. FacebookUser (by facebookId)
        if (dto.getFacebookUser() != null && (entity.getFacebookUser() == null ||
                !dto.getFacebookUser().equals(entity.getFacebookUser().getFacebookId()))) {
            FacebookUser fb = new FacebookUser();
            fb.setFacebookId(dto.getFacebookUser());
            entity.setFacebookUser(fb);
        }

        return entity;
    }
}
