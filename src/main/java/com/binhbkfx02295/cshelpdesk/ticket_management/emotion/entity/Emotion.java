package com.binhbkfx02295.cshelpdesk.ticket_management.emotion.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Emotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String code;    // e.g., ANGRY, HAPPY, SAD
    private String name;    // e.g., Tức giận, Hài lòng, Buồn
}
