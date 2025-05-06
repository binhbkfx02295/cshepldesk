package com.binhbkfx02295.cshelpdesk.ticket_management.category.repository;

import com.binhbkfx02295.cshelpdesk.ticket_management.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    boolean existsByCode(String code);
}
