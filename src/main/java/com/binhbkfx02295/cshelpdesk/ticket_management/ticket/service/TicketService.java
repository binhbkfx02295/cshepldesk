package com.binhbkfx02295.cshelpdesk.ticket_management.ticket.service;

import com.binhbkfx02295.cshelpdesk.ticket_management.note.dto.NoteDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDashboardDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDetailDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketSearchCriteria;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.util.PaginationResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;


public interface TicketService {

    APIResultSet<TicketDetailDTO> createTicket(TicketDetailDTO dto);
    APIResultSet<TicketDetailDTO> updateTicket(int id, TicketDetailDTO dto);
    APIResultSet<TicketDetailDTO> getTicketById(int id);
    APIResultSet<Void> addTagToTicket(int ticketId, int hashtagId);
    APIResultSet<Void> removeTagFromTicket(int ticketId, int hashtagId);
    APIResultSet<PaginationResponse<TicketDetailDTO>> searchTickets(TicketSearchCriteria criteria, Pageable pageable);
    APIResultSet<TicketDetailDTO> findLatestByFacebookUserId(String facebookId);
    APIResultSet<List<TicketDTO>> findAllByFacebookUserId(String facebookId);
    APIResultSet<Void> addNoteToTicket(int ticketId, NoteDTO note);
    APIResultSet<Void> deleteNoteFromTicket(int ticketId, int noteId);
    APIResultSet<Set<NoteDTO>> getNotes(int ticketId);
    APIResultSet<Void> updateCategoryForTicket(int ticketId, int categoryId);
    APIResultSet<Void> removeCategoryFromTicket(int ticketId, int categoryId);
    APIResultSet<Void> deleteById(int ticketId);
    APIResultSet<List<TicketDashboardDTO>> getForDashboard();
}
