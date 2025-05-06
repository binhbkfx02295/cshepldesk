package com.binhbkfx02295.cshelpdesk.employee_management.employee.entity;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.StatusLog;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Status{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @OneToMany(mappedBy = "status", cascade = CascadeType.ALL)
    private List<StatusLog> statusLogs = new ArrayList<>();
}
