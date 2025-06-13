package com.binhbkfx02295.cshelpdesk.ticket_management.performance.repository;

import com.binhbkfx02295.cshelpdesk.ticket_management.performance.model.Criteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CriteriaRepository extends JpaRepository<Criteria, Long> {
    Optional<Criteria> findByCodeAndActiveTrue(String code);
    List<Criteria> findAllByActiveTrue();
}
