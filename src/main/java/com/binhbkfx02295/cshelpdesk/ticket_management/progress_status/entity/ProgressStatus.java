package com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String code;     // e.g., OPEN, IN_PROGRESS, CLOSED
    private String name;     // e.g., Đang mở, Đang xử lý, Đã đóng
}
