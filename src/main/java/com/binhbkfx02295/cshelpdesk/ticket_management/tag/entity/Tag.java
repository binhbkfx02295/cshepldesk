package com.binhbkfx02295.cshelpdesk.ticket_management.tag.entity;

import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String name;

    @ManyToMany(mappedBy ="tags")
    private List<Ticket> tickets;
}
