package com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.service;

import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.dto.SatisfactionDTO;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import java.util.List;

public interface SatisfactionService {
    APIResultSet<List<SatisfactionDTO>> getAllSatisfaction();
}
