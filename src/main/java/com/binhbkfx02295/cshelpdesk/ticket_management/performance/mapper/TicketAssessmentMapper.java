package com.binhbkfx02295.cshelpdesk.ticket_management.performance.mapper;

import com.binhbkfx02295.cshelpdesk.ticket_management.performance.dto.TicketAssessmentDetailDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.dto.TicketAssessmentListDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.model.TicketAssessment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TicketAssessmentMapper {

    private final CriteriaMapper criteriaMapper;

    public TicketAssessmentDetailDTO toDetailDTO(TicketAssessment entity) {
        TicketAssessmentDetailDTO dto = new TicketAssessmentDetailDTO();
        dto.setId(entity.getId());
        dto.setAssignee(entity.getEvaluatedAssignee());
        dto.setTicketId(entity.getTicketId());
        dto.setEvaluatedBy(entity.getEvaluatedBy());
        dto.setEvaluatedAt(entity.getEvaluatedAt().getTime());
        dto.setPassed(entity.isPassed());
        dto.setFirstResponseTime(entity.getFirstResponseTime());
        dto.setAvgResponseTime(entity.getAvgResponseTime());
        dto.setResolutionTime(entity.getResolutionTime());
        dto.setSummary(entity.getSummary());
        dto.setCriterias(entity.getFailedCriterias().stream().map(criteriaMapper::toDTO).toList());
        return dto;
    }

    public TicketAssessmentListDTO toListDTO(TicketAssessment entity) {
        TicketAssessmentListDTO dto = new TicketAssessmentListDTO();
        dto.setTicketId(entity.getTicketId());
        dto.setAssigneeUsername(entity.getEvaluatedAssignee());
        dto.setEvaluatedBy(entity.getEvaluatedBy());
        dto.setEvaluatedAt(entity.getEvaluatedAt().getTime());
        dto.setPassed(entity.isPassed());
        return dto;
    }

    public List<TicketAssessmentListDTO> toListDTO(List<TicketAssessment> ticketAssessments) {
        return ticketAssessments.stream().map(this::toListDTO).toList();
    }

    public TicketAssessment mergeToEntity(TicketAssessment ticketAssessment, TicketAssessmentDetailDTO dto) {
        ticketAssessment.setSummary(dto.getSummary());
        return ticketAssessment;
    }
}
