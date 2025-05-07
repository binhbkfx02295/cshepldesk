package com.binhbkfx02295.cshelpdesk.facebookuser.entity;

import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import jakarta.persistence.*;
import lombok.*;

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
    private String facebookId;
    private String facebookFirstName;
    private String facebookLastName;
    private String facebookProfilePic;
    private String email;
    private String phone;
    private String zalo;

    @OneToMany(mappedBy = "facebookUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();

}
