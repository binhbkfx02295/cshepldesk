package com.binhbkfx02295.cshelpdesk.ticket_management.emotion.controller;

import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.dto.EmotionDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.service.EmotionServiceImpl;
import com.binhbkfx02295.cshelpdesk.util.APIResponseEntityHelper;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/emotion")
@Slf4j
@RequiredArgsConstructor
public class EmotionController {

    private final EmotionServiceImpl emotionService;

    @GetMapping
    public ResponseEntity<APIResultSet<List<EmotionDTO>>> getAllEmotion() {
        return APIResponseEntityHelper.from(emotionService.getAllEmotion());
    }
}
