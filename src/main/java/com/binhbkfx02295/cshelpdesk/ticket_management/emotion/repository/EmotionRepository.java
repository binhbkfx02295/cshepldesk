package com.binhbkfx02295.cshelpdesk.ticket_management.emotion.repository;

import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.entity.Emotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmotionRepository extends JpaRepository<Emotion, Integer> {
    boolean existsByCode(String code);
}
