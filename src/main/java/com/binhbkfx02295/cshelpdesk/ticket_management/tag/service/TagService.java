package com.binhbkfx02295.cshelpdesk.ticket_management.tag.service;

import com.binhbkfx02295.cshelpdesk.ticket_management.tag.dto.TagDTO;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;

import java.util.List;

public interface TagService {
    APIResultSet<TagDTO> create(TagDTO tag);
    APIResultSet<TagDTO> update(int id, TagDTO tag);
    APIResultSet<Void> delete(int id);
    APIResultSet<List<TagDTO>> search(String keyword);
    APIResultSet<List<TagDTO>> getAll();
}
