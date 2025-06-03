package com.binhbkfx02295.cshelpdesk.message.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String type;
    @Column(columnDefinition="TEXT")
    private String url;

    private Long stickerId;

    @ManyToOne
    private Message message;
}
