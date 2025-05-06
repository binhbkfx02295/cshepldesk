package com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.repository;

import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.entity.ProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgressStatusRepository extends JpaRepository<ProgressStatus, Integer> {
    boolean existsByCode(String code);
}
