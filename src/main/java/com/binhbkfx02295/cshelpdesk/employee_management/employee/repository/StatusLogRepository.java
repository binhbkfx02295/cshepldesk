package com.binhbkfx02295.cshelpdesk.employee_management.employee.repository;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.StatusLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StatusLogRepository extends JpaRepository<StatusLog, Integer> {

    Optional<StatusLog> findFirstByEmployee_UsernameOrderByTimestampDesc(String username);


}
