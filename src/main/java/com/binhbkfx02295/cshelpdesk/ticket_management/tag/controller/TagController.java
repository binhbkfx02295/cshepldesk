package com.binhbkfx02295.cshelpdesk.ticket_management.tag.controller;

import com.binhbkfx02295.cshelpdesk.ticket_management.tag.service.TagService;
import com.binhbkfx02295.cshelpdesk.ticket_management.tag.dto.TagDTO;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResponseEntityHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tag")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping("/search")
    public ResponseEntity<APIResultSet<List<TagDTO>>> search(@RequestParam String keyword) {
        return APIResponseEntityHelper.from(tagService.search(keyword));
    }

    @GetMapping()
    public ResponseEntity<APIResultSet<List<TagDTO>>> getAll() {
        return APIResponseEntityHelper.from(tagService.getAll());
    }

    @PostMapping
    public ResponseEntity<APIResultSet<TagDTO>> create(@RequestBody TagDTO tagDTO) {
        APIResultSet<TagDTO> result = tagService.create(tagDTO);
        return APIResponseEntityHelper.from(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResultSet<TagDTO>> update(@PathVariable int id, @RequestBody TagDTO tagDTO) {
        APIResultSet<TagDTO> result = tagService.update(id, tagDTO);
        return APIResponseEntityHelper.from(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResultSet<Void>> delete(@PathVariable int id) {
        APIResultSet<Void> result = tagService.delete(id);
        return APIResponseEntityHelper.from(result);
    }
}
