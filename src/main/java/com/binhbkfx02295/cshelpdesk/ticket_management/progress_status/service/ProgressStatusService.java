package com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.service;

import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.dto.ProgressStatusDTO;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;
import java.util.List;
public interface ProgressStatusService {
    APIResultSet<List<ProgressStatusDTO>> getAllProgressStatus();
}
