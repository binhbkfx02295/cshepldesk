package com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SatisfactionDTO {
    private int id;

    private String code;       // e.g., 1 to 5 stars
    private String name;  // Optional feedback
}
