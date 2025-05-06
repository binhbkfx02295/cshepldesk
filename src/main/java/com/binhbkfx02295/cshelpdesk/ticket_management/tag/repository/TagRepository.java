package com.binhbkfx02295.cshelpdesk.ticket_management.tag.repository;

import com.binhbkfx02295.cshelpdesk.ticket_management.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
    boolean existsByName(String name);
    List<Tag> findByNameContainingIgnoreCase(String keyword);
}
