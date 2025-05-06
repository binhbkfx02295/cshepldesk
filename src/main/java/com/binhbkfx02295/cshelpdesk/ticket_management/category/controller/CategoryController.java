package com.binhbkfx02295.cshelpdesk.ticket_management.category.controller;

import com.binhbkfx02295.cshelpdesk.ticket_management.category.dto.CategoryDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.category.service.CategoryService;
import com.binhbkfx02295.cshelpdesk.util.APIResponseEntityHelper;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<APIResultSet<List<CategoryDTO>>> getAll() {
        return APIResponseEntityHelper.from(categoryService.getAll());
    }

    @PostMapping
    public ResponseEntity<APIResultSet<CategoryDTO>> create(@RequestBody CategoryDTO categoryDTO) {
        return APIResponseEntityHelper.from(categoryService.create(categoryDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResultSet<CategoryDTO>> update(@PathVariable int id, @RequestBody CategoryDTO categoryDTO) {
        return APIResponseEntityHelper.from(categoryService.update(id, categoryDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResultSet<Void>> delete(@PathVariable int id) {
        return APIResponseEntityHelper.from(categoryService.delete(id));
    }
}
