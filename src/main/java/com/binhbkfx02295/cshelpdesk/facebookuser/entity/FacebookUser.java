package com.binhbkfx02295.cshelpdesk.facebookuser.entity;

import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "facebook_user")
public class FacebookUser {
    @Id
    @Column(nullable = false)
    private String facebookId;

    @Column(nullable = false)
    private String facebookName;

    @Column(nullable = false)
    private String facebookProfilePic;
    private String realName;
    private String email;
    private String phone;
    private String zalo;

    @CreationTimestamp
    private Instant createdAt;

    @OneToMany(mappedBy = "facebookUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();

}
