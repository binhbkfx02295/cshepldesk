package com.binhbkfx02295.cshelpdesk.ticket_management.ticket.mapper;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.repository.EmployeeRepository;
import com.binhbkfx02295.cshelpdesk.facebookuser.repository.FacebookUserRepository;
import com.binhbkfx02295.cshelpdesk.infrastructure.common.cache.MasterDataCache;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.mapper.EmployeeMapper;
import com.binhbkfx02295.cshelpdesk.facebookuser.entity.FacebookUser;
import com.binhbkfx02295.cshelpdesk.facebookuser.mapper.FacebookUserMapper;
import com.binhbkfx02295.cshelpdesk.message.dto.MessageDTO;
import com.binhbkfx02295.cshelpdesk.message.mapper.MessageMapper;
import com.binhbkfx02295.cshelpdesk.ticket_management.category.entity.Category;
import com.binhbkfx02295.cshelpdesk.ticket_management.category.mapper.CategoryMapper;
import com.binhbkfx02295.cshelpdesk.ticket_management.category.repository.CategoryRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.entity.Emotion;
import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.mapper.EmotionMapper;
import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.repository.EmotionRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.note.mapper.NoteMapper;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.entity.ProgressStatus;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.mapper.ProgressStatusMapper;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.repository.ProgressStatusRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.entity.Satisfaction;
import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.mapper.SatisfactionMapper;
import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.repository.SatisfactionRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.tag.mapper.TagMapper;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketListDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDashboardDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDetailDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketReportDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
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
    private final NoteMapper noteMapper;

    private final CategoryRepository categoryRepository;
    private final EmotionRepository emotionRepository;
    private final SatisfactionRepository satisfactionRepository;
    private final EmployeeRepository employeeRepository;
    private final ProgressStatusRepository progressStatusRepository;
    private final FacebookUserRepository facebookUserRepository;

    public TicketListDTO toListDTO(Ticket ticket) {
        if (ticket == null) return null;
        TicketListDTO dto = new TicketListDTO();

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
        ticket.setAssignee(dto.getAssignee() != null ? employeeRepository.getReferenceById(dto.getAssignee().getUsername()) : null);
        ticket.setProgressStatus(progressStatusRepository.getReferenceById(dto.getProgressStatus().getId()));
        ticket.setCategory(dto.getCategory() != null ? categoryRepository.getReferenceById(dto.getCategory().getId()) : null);
        ticket.setEmotion(dto.getEmotion() != null ? emotionRepository.getReferenceById(dto.getEmotion().getId()) : null);
        ticket.setSatisfaction(dto.getSatisfaction() != null ? satisfactionRepository.getReferenceById(dto.getSatisfaction().getId()) : null);
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
                !dto.getAssignee().getUsername().equals(entity.getAssignee().getUsername()))) {
            Employee assignee = employeeRepository.getReferenceById(dto.getAssignee().getUsername());
            entity.setAssignee(assignee);
        }

        // 4. ProgressStatus (name hoặc code)
        if (dto.getProgressStatus().getId() != (entity.getProgressStatus().getId())) {
            ProgressStatus status = progressStatusRepository.getReferenceById(dto.getProgressStatus().getId());
            entity.setProgressStatus(status);
        }

        // 5. Category (name)
        if (dto.getCategory() != null && (entity.getCategory() == null ||
                dto.getCategory().getId() != (entity.getCategory().getId()))) {
            Category category = categoryRepository.getReferenceById(dto.getCategory().getId());
            entity.setCategory(category);
        }

        // 6. Emotion (by ID)
        if (dto.getEmotion() != null && (entity.getEmotion() == null ||
                dto.getEmotion().getId() != entity.getEmotion().getId())) {
            Emotion emotion = emotionRepository.getReferenceById(dto.getEmotion().getId());
            entity.setEmotion(emotion);
        }

        // 7. Satisfaction (by score)
        if (dto.getSatisfaction() != null && (entity.getSatisfaction() == null ||
                dto.getSatisfaction().getId() != entity.getSatisfaction().getId())) {
            Satisfaction satisfaction = satisfactionRepository.getReferenceById(dto.getSatisfaction().getId());
            entity.setSatisfaction(satisfaction);
        }

        // 8. FacebookUser (by facebookId)
        if (dto.getFacebookUser() != null && (entity.getFacebookUser() == null ||
                !dto.getFacebookUser().getFacebookId().equals(entity.getFacebookUser().getFacebookId()))) {
            FacebookUser fb = facebookUserRepository.getReferenceById(dto.getFacebookUser().getFacebookId());
            entity.setFacebookUser(fb);
        }

        // 9. Tags
        //TODO: implement later

        // 10.Notes
        //TODO: implement later
        return entity;
    }

    public TicketDashboardDTO toDashboardDTO(Ticket entity) {
        if (entity == null) {
            return null;
        }
        TicketDashboardDTO dto = new TicketDashboardDTO();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setAssignee(entity.getAssignee() != null ? employeeMapper.toTicketDTO(entity.getAssignee()) : null);
        dto.setFacebookUser(entity.getFacebookUser() != null ? facebookUserMapper.toListDTO(entity.getFacebookUser()) : null);
        dto.setProgressStatus(progressStatusMapper.toDTO(entity.getProgressStatus()));
        dto.setTitle(entity.getTitle());
        if (entity.getMessages() != null && !entity.getMessages().isEmpty() && entity.getProgressStatus().getId() != 3) {
            dto.setHasNewMessage(!entity.getMessages().get(entity.getMessages().size()-1).isSenderEmployee());
        }
        return dto;
    }

    public TicketReportDTO toReportDTO(Ticket entity) {
        TicketReportDTO dto = new TicketReportDTO();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setResolutionRate(entity.getResolutionRate());
        dto.setFirstResponseRate(entity.getFirstResponseRate());
        dto.setOverallResponseRate(entity.getOverallResponseRate());
        return dto;
    }
}
