package com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.controller;

import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.dto.SatisfactionDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.service.SatisfactionServiceImpl;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResponseEntityHelper;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/satisfaction")
@RequiredArgsConstructor

public class SatisfactionController {

    private final SatisfactionServiceImpl satisfactionService;

    @GetMapping
    public ResponseEntity<APIResultSet<List<SatisfactionDTO>>> getAllSatisfaction() {
        return APIResponseEntityHelper.from(satisfactionService.getAllSatisfaction());
    }
}
