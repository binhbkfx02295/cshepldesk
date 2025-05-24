package com.binhbkfx02295.cshelpdesk.ticket_management.report.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TicketKPIReport {

    private float firstResponseTime;
    private float avgResponseTime;
    private float resolutionTime;

}
