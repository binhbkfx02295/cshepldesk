package com.binhbkfx02295.cshelpdesk.ticket_management.category.mapper;

import com.binhbkfx02295.cshelpdesk.ticket_management.category.entity.Category;
import com.binhbkfx02295.cshelpdesk.ticket_management.category.dto.CategoryDTO;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryDTO toDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setName(category.getName());
        dto.setId(category.getId());
        dto.setCode(category.getCode());
        return dto;
    };

    public Category toEntity(CategoryDTO dto) {
        Category entity = new Category();
        entity.setId(dto.getId());
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        return entity;
    }
}
