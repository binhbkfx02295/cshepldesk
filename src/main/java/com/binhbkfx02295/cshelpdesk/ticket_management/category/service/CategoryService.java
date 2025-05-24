package com.binhbkfx02295.cshelpdesk.ticket_management.category.service;

import com.binhbkfx02295.cshelpdesk.ticket_management.category.dto.CategoryDTO;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;

import java.util.List;

public interface CategoryService {
    APIResultSet<CategoryDTO> create(CategoryDTO category);
    APIResultSet<CategoryDTO> update(int id, CategoryDTO category);
    APIResultSet<Void> delete(int id);
    APIResultSet<List<CategoryDTO>> getAll();
}
