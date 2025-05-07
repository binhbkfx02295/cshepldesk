package com.binhbkfx02295.cshelpdesk.ticket_management.ticket.service;

import com.binhbkfx02295.cshelpdesk.common.cache.MasterDataCache;
import com.binhbkfx02295.cshelpdesk.facebookuser.service.FacebookUserService;
import com.binhbkfx02295.cshelpdesk.facebookuser.service.FacebookUserServiceImpl;
import com.binhbkfx02295.cshelpdesk.ticket_management.category.entity.Category;
import com.binhbkfx02295.cshelpdesk.ticket_management.category.repository.CategoryRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.note.entity.Note;
import com.binhbkfx02295.cshelpdesk.ticket_management.note.dto.NoteDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.note.repository.NoteRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDetailDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketSearchCriteria;
import com.binhbkfx02295.cshelpdesk.ticket_management.tag.entity.Tag;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import com.binhbkfx02295.cshelpdesk.ticket_management.tag.repository.TagRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.repository.TicketRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.mapper.TicketMapper;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.service.EmployeeService;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.spec.TicketSpecification;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.util.PaginationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TagRepository tagRepository;
    private final TicketMapper mapper;
    private final NoteRepository noteRepository;
    private final CategoryRepository categoryRepository;
    private final FacebookUserServiceImpl facebookUserService;
    private final MasterDataCache cache;

    @Override
    public APIResultSet<TicketDetailDTO> createTicket(TicketDetailDTO dto) {
        APIResultSet<TicketDetailDTO> validationResult = validateTicketDTO(dto);
        if (validationResult.getHttpCode() != 200) {
            log.error(validationResult.getMessage());
            return validationResult;
        }

        Ticket saved = ticketRepository.save(mapper.toEntity(dto));
        APIResultSet<TicketDetailDTO> result = APIResultSet.ok("Created successfully", mapper.toDetailDTO(saved));
        log.info(result.getMessage());
        return result;
    }

    @Override
    public APIResultSet<TicketDetailDTO> updateTicket(int id, TicketDetailDTO dto) {
        Optional<Ticket> existingOpt = ticketRepository.findByIdWithDetails(id);
        if (existingOpt.isEmpty()) {
            return APIResultSet.notFound("Ticket not found");
        }

        APIResultSet<TicketDetailDTO> validationResult = validateTicketDTO(dto);
        if (validationResult.getHttpCode() != 200) {
            return validationResult;
        }

        Ticket existing = existingOpt.get();
        mapper.mergeToEntity(dto, existing);
        Ticket saved = ticketRepository.save(existing);
        APIResultSet<TicketDetailDTO> result = APIResultSet.ok("Updated successfully", mapper.toDetailDTO(saved));
        log.info(result.getMessage());
        return result;
    }


    @Override
    public APIResultSet<TicketDetailDTO> getTicketById(int id) {
        Optional<Ticket> ticketOpt = ticketRepository.findByIdWithDetails(id);
        return ticketOpt.map(ticket -> {
            APIResultSet<TicketDetailDTO> result = APIResultSet.ok("Found", mapper.toDetailDTO(ticket));
            log.info(result.getMessage());
            return result;
        }).orElseGet(() -> {
            APIResultSet<TicketDetailDTO> result = APIResultSet.notFound("Ticket not found");
            log.info(result.getMessage());
            return result;

        });
    }

    @Override
    public APIResultSet<Void> addTagToTicket(int ticketId, int hashtagId) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
        Optional<Tag> tagOpt = tagRepository.findById(hashtagId);
        if (ticketOpt.isEmpty() || tagOpt.isEmpty()) {
            return APIResultSet.notFound("Ticket or Tag not found");
        }
        Ticket ticket = ticketOpt.get();
        ticket.getTags().add(tagOpt.get());
        APIResultSet<Void> result = APIResultSet.ok("Tag added", null);
        log.info(result.getMessage());
        return result;
    }

    @Override
    public APIResultSet<Void> removeTagFromTicket(int ticketId, int tagId) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
        Optional<Tag> tagOpt = tagRepository.findById(tagId);
        if (ticketOpt.isEmpty() || tagOpt.isEmpty()) {
            return APIResultSet.notFound("Ticket or Tag not found");
        }
        Ticket ticket = ticketOpt.get();
        ticket.getTags().remove(tagOpt.get());
        APIResultSet<Void> result = APIResultSet.ok("Tag removed", null);
        return result;
    }

    @Override
    public APIResultSet<TicketDetailDTO> findLatestByFacebookUserId(String facebookId) {
        try {
            Optional<Ticket> ticket = ticketRepository
                    .findFirstByFacebookUser_FacebookIdOrderByCreatedAtDesc(facebookId);

            return ticket
                    .map(value -> {
                        APIResultSet<TicketDetailDTO> result = APIResultSet.ok("Đã tìm thấy ticket mới nhất", mapper.toDetailDTO(value));
                        log.info(result.getMessage());
                        return result;
                    })
                    .orElseGet(() -> {
                        APIResultSet<TicketDetailDTO> result = APIResultSet.notFound("Không tìm thấy ticket.");
                        log.info(result.getMessage());
                        return result;
                    });
        } catch (Exception e) {
            log.error("Lỗi tìm ticket theo facebook user id: {} {}", e, e.getStackTrace());
            return APIResultSet.internalError("Lỗi tìm ticket theo facebook user id");
        }
    }

    @Override
    public APIResultSet<List<TicketDTO>> findAllByFacebookUserId(String facebookId) {
        try {
            List<TicketDTO> tickets = ticketRepository.findAllByFacebookUser_FacebookId(facebookId);
            APIResultSet<List<TicketDTO>> result = APIResultSet.ok("OK", tickets);
            log.info(result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("Lỗi server" , e);
            return APIResultSet.internalError();
        }
    }

    @Override
    public APIResultSet<Void> addNoteToTicket(int ticketId, NoteDTO noteDTO) {
        try {
            Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
            if (ticketOpt.isEmpty()) {
                return APIResultSet.notFound("Không tìm thấy ticket.");
            }
            if (!ticketRepository.existsById(ticketId)) {
                return APIResultSet.notFound("Không tìm thấy ticket.");
            }
            noteDTO.setTicketId(ticketId);
            noteRepository.save(noteDTO.toEntity());
            APIResultSet<Void> result = APIResultSet.ok("Thêm note thành công", null);
            log.info(result.getMessage());
            return result;

        } catch (Exception e) {
            log.error("Lỗi thêm note vào ticket", e);
            return APIResultSet.internalError("Lỗi thêm note vào ticket");
        }
    }

    @Override
    public APIResultSet<Void> deleteNoteFromTicket(int ticketId, int noteId) {
        try {
            if (!noteRepository.existsByIdAndTicket_Id(noteId, ticketId)) {
                APIResultSet<Void> result = APIResultSet.notFound("Không tìm thấy note hoặc ticket id.");
                log.info(result.getMessage());
                return result;
            }

            noteRepository.deleteByIdAndTicket_Id(noteId, ticketId);
            APIResultSet<Void> result = APIResultSet.ok("Xóa note thành công.", null);
            log.info(result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("Lỗi server không thể xóa note", e);
            return APIResultSet.internalError("Lỗi server không thể xóa note");
        }
    }

    @Override
    public APIResultSet<Set<NoteDTO>> getNotes(int ticketId) {
        try {
            if (!noteRepository.existsByTicket_Id(ticketId)) {
                return APIResultSet.notFound("Không tìm thấy ticket.");
            }

            Set<NoteDTO> notes = noteRepository.findAllByTicket_Id(ticketId).stream()
                    .map(Note::toDTO).collect(Collectors.toSet());

            APIResultSet<Set<NoteDTO>> result = APIResultSet.ok("Danh sách notes", notes);
            log.info(result.getMessage());
            return result;

        } catch (Exception e) {
            log.error("Lỗi server lấy tất cả note của ticket", e);
            return APIResultSet.internalError("Lỗi server lấy tất cả note của ticket");
        }
    }

    @Override
    public APIResultSet<PaginationResponse<TicketDTO>> searchTickets(TicketSearchCriteria criteria, Pageable pageable) {
        try {
            var spec = TicketSpecification.build(criteria);
            var page = ticketRepository.findAll(spec, pageable);
            List<TicketDTO> dtoList = page.getContent().stream()
                    .map(mapper::toListDTO)
                    .collect(Collectors.toList());
            PaginationResponse<TicketDTO> pagination = new PaginationResponse<>(
                    dtoList,
                    page.getNumber(),
                    page.getSize(),
                    page.getTotalElements(),
                    page.getTotalPages()
            );
            APIResultSet<PaginationResponse<TicketDTO>> result = APIResultSet.ok("Tim ticket ok", pagination);
            log.info(result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("Lỗi search ticket", e);
            return APIResultSet.internalError("Không thể thực hiện tìm kiếm.");
        }
    }

    private APIResultSet<TicketDetailDTO> validateTicketDTO(TicketDetailDTO dto) {
        if (dto.getAssignee() != null &&
                cache.getEmployee(dto.getAssignee()) == null) {
            return APIResultSet.badRequest("Lỗi Staff không tồn tại: " + dto.getAssignee());
        }

        if (dto.getFacebookUser() == null) {
            return APIResultSet.badRequest("Lỗi thieesu facebook user");
        } else {
            APIResultSet<Void> result = facebookUserService.existsById(dto.getFacebookUser());
            if (result.getHttpCode() != 200) {
                return APIResultSet.badRequest(result.getMessage());
            }
        }

        if (dto.getProgressStatus() == null ||
                cache.getProgress(dto.getProgressStatus()) == null) {
            return APIResultSet.badRequest("Invalid progress code: " + dto.getProgressStatus());
        }

        if (dto.getCategory() != null &&
                cache.getCategory(dto.getCategory()) == null) {
            return APIResultSet.badRequest("Invalid category code: " + dto.getCategory());
        }

        if (dto.getEmotion() != 0 &&
                cache.getEmotion(dto.getEmotion()) == null) {
            return APIResultSet.badRequest("Invalid emotion code: " + dto.getEmotion());
        }

        if (dto.getSatisfaction() != 0 &&
                cache.getSatisfaction(dto.getSatisfaction()) == null) {
            return APIResultSet.badRequest("Invalid satisfaction score: " + dto.getSatisfaction());
        }

        if (dto.getTags() != null) {
            for (String tag : dto.getTags()) {
                if (cache.getTag(tag) == null) {
                    return APIResultSet.badRequest("Invalid hashtag: " + tag);
                }
            }
        }

        return APIResultSet.ok("Validation passed", null);
    }
    @Override
    public APIResultSet<Void> updateCategoryForTicket(int ticketId, int categoryId) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (ticketOpt.isEmpty()) return APIResultSet.notFound("Ticket không tồn tại");
        if (categoryOpt.isEmpty()) return APIResultSet.notFound("Category không tồn tại");

        Ticket ticket = ticketOpt.get();
        ticket.setCategory(categoryOpt.get());
        ticketRepository.save(ticket);

        return APIResultSet.ok("Cập nhật category thành công", null);
    }

    @Override
    public APIResultSet<Void> removeCategoryFromTicket(int ticketId, int categoryId) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);

        if (ticketOpt.isEmpty()) return APIResultSet.notFound("Ticket không tồn tại");

        Ticket ticket = ticketOpt.get();
        if (ticket.getCategory() == null || ticket.getCategory().getId() == categoryId) {
            return APIResultSet.badRequest("Ticket không chứa category cần xóa");
        }

        ticket.setCategory(null);
        ticketRepository.save(ticket);

        return APIResultSet.ok("Đã xóa category khỏi ticket", null);
    }

    @Override
    public APIResultSet<Void> deleteById(int ticketId) {
        try {
            ticketRepository.deleteById(ticketId);
            log.info("Delete ticket id {} OK", ticketId);
            return APIResultSet.ok(String.format("Delete ticket id %d OK", ticketId), null);
        }
        catch(Exception e) {
            return APIResultSet.internalError();
        }
    }

}

