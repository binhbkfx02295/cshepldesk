package com.binhbkfx02295.cshelpdesk.employee_management.permission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    boolean existsByName(String permissiom);
}
