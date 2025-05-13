package com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.EmployeeDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.category.dto.CategoryDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.dto.EmotionDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.dto.ProgressStatusDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.dto.SatisfactionDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.tag.dto.TagDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketDetailDTO {
    private int id;
    private String title;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp closedAt;
    private ProgressStatusDTO progressStatus;
    private CategoryDTO category;
    private EmployeeDTO assignee;
    private EmotionDTO emotion;
    private SatisfactionDTO satisfaction;
    private FacebookUserDTO facebookUser;
    private List<TagDTO> tags;
}
