package com.binhbkfx02295.cshelpdesk.facebookgraphapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class FacebookToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String pageId;
    private String longLivedAccessToken;
    private LocalDateTime lastUpdated;
    private LocalDateTime expiresAt;
}