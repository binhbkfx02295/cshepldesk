package com.binhbkfx02295.cshelpdesk.ticket_management.performance.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Criteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 32)
    private String code;

    @Column(nullable = false, length = 64)
    private String name;

    @Column(nullable = false, length = 256)
    private String description;

    private boolean active;
}
