package com.binhbkfx02295.cshelpdesk.ticket_management.emotion.service;

import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.dto.EmotionDTO;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;

import java.util.List;

public interface EmotionService {

    APIResultSet<List<EmotionDTO>> getAllEmotion();
}
