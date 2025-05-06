package com.binhbkfx02295.cshelpdesk.employee_management.employee.repository;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface StatusRepository extends JpaRepository<Status, Integer> {
    boolean existsByName(String name);
}
