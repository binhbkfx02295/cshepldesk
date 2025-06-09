package com.binhbkfx02295.cshelpdesk.employee_management.usergroup;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee;
import com.binhbkfx02295.cshelpdesk.employee_management.permission.Permission;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int groupId;
    private String name;
    private String code;
    private String description;
    @OneToMany(mappedBy = "userGroup")
    private List<Employee> users;

    @ManyToMany()
    @JoinTable(
            name = "usergroup_permission",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions;

}
