package com.binhbkfx02295.cshelpdesk.ticket_management.emotion.mapper;

import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.entity.Emotion;
import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.dto.EmotionDTO;

public interface EmotionMapper {

    EmotionDTO toDTO(Emotion emotion);
}
