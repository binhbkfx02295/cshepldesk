package com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Satisfaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int score;       // e.g., 1 to 5 stars
    private String comment;  // Optional feedback
}
