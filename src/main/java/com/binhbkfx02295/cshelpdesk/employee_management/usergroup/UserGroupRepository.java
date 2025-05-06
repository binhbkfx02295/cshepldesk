package com.binhbkfx02295.cshelpdesk.employee_management.usergroup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Integer> {
    boolean existsByName(String name);
}
