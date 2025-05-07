package com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.repository;

import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.entity.Satisfaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SatisfactionRepository extends JpaRepository<Satisfaction, Integer> {
    boolean existsByScore(int score);
}
