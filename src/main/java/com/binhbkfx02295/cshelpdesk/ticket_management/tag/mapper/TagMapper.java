package com.binhbkfx02295.cshelpdesk.ticket_management.tag.mapper;

import com.binhbkfx02295.cshelpdesk.ticket_management.tag.entity.Tag;
import com.binhbkfx02295.cshelpdesk.ticket_management.tag.dto.TagDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import org.springframework.stereotype.Component;

@Component
public class TagMapper {

     public TagDTO toDTO(Tag tag) {
          TagDTO dto = new TagDTO();
          dto.setId(tag.getId());
          dto.setName(tag.getName());
          return dto;
     };

     public Tag toEntity(TagDTO dto) {
          Tag tag = new Tag();
          tag.setName(dto.getName());
          tag.setId(dto.getId());
          return tag;
     };

}
