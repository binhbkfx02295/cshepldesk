package com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.controller;

import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.dto.ProgressStatusDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.service.ProgressStatusServiceImpl;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResponseEntityHelper;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping(value = "/api/progress-status")
@RequiredArgsConstructor
@Slf4j
public class ProgressStatusController {

    private final ProgressStatusServiceImpl progressStatusService;

    @GetMapping
    public ResponseEntity<APIResultSet<List<ProgressStatusDTO>>> getAllProgressStatus() {
        return APIResponseEntityHelper.from(progressStatusService.getAllProgressStatus());
    }
}
