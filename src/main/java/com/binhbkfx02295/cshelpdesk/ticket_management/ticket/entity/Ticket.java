package com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee;
import com.binhbkfx02295.cshelpdesk.facebookuser.entity.FacebookUser;
import com.binhbkfx02295.cshelpdesk.message.entity.Message;
import com.binhbkfx02295.cshelpdesk.ticket_management.category.entity.Category;
import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.entity.Emotion;
import com.binhbkfx02295.cshelpdesk.ticket_management.note.entity.Note;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.entity.ProgressStatus;
import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.entity.Satisfaction;
import com.binhbkfx02295.cshelpdesk.ticket_management.tag.entity.Tag;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;

    private Timestamp createdAt;
    private Timestamp lastUpdateAt;
    private Timestamp closedAt;

    // Người xử lý (username hoặc tên người dùng)
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee assignee;

    // khách hàng
    @ManyToOne
    @JoinColumn(name = "facebook_user_id", nullable = false)
    private FacebookUser facebookUser;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "progress_status_id", nullable = false)
    private ProgressStatus progressStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emotion_id")
    private Emotion emotion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "satisfaction_id")
    private Satisfaction satisfaction;


    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "ticket_tag",
            joinColumns = @JoinColumn(name = "ticket_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "ticket", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Note> notes = new ArrayList<>();


    @OneToMany(mappedBy = "ticket", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();

    private Long firstResponseRate;
    private Long overallResponseRate;
    private Long resolutionRate;
    private int tokenUsed;
    private float price;
    private String gptModelUsed;

}
