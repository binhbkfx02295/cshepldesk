package com.binhbkfx02295.cshelpdesk.ticket_management.note.mapper;

import com.binhbkfx02295.cshelpdesk.ticket_management.note.entity.Note;
import com.binhbkfx02295.cshelpdesk.ticket_management.note.dto.NoteDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import org.springframework.stereotype.Component;

@Component
public class NoteMapper {

    public NoteDTO toDTO(Note note) {
        NoteDTO dto = new NoteDTO();
        dto.setId(note.getId());
        dto.setText(note.getText());
        dto.setTimestamp(note.getTimestamp());
        dto.setTicketId(note.getTicket().getId());
        return dto;
    };

    public Note toEntity(NoteDTO dto) {
        Note entity = new Note();
        entity.setId(dto.getId());
        entity.setText(dto.getText());
        entity.setTimestamp(dto.getTimestamp());

        if (dto.getTicketId() != 0) {
            Ticket ticket = new Ticket();
            ticket.setId(0);
            entity.setTicket(ticket);
        }

        return entity;
    }
}
