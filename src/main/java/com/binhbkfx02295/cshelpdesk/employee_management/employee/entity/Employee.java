package com.binhbkfx02295.cshelpdesk.employee_management.employee.entity;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.EmployeeDTO;
import com.binhbkfx02295.cshelpdesk.employee_management.usergroup.UserGroup;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Employee {

    private String name;
    @Id
    private String username;
    private String password;

    @ColumnDefault("0")
    private int failedLoginCount;

    @ColumnDefault("true")
    private boolean active;
    private String description;

    @CreationTimestamp
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private UserGroup userGroup;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StatusLog> statusLogs = new ArrayList<>();


}
