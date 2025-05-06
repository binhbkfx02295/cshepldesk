package com.binhbkfx02295.cshelpdesk.ticket_management.category.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String code;   // e.g., PRODUCT, DELIVERY
    private String name;   // e.g., Sản phẩm, Giao hàng
}
