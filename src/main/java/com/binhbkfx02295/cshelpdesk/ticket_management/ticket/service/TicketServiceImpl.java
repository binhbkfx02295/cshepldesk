package com.binhbkfx02295.cshelpdesk.ticket_management.ticket.service;

import com.binhbkfx02295.cshelpdesk.infrastructure.common.cache.MasterDataCache;
import com.binhbkfx02295.cshelpdesk.facebookuser.service.FacebookUserServiceImpl;
import com.binhbkfx02295.cshelpdesk.message.dto.MessageDTO;
import com.binhbkfx02295.cshelpdesk.message.service.MessageServiceImpl;
import com.binhbkfx02295.cshelpdesk.ticket_management.note.entity.Note;
import com.binhbkfx02295.cshelpdesk.ticket_management.note.dto.NoteDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.note.repository.NoteRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.tag.dto.TagDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.*;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import com.binhbkfx02295.cshelpdesk.ticket_management.tag.repository.TagRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.repository.TicketRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.mapper.TicketMapper;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.spec.TicketSpecification;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.PaginationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
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
    private final FacebookUserServiceImpl facebookUserService;
    private final MasterDataCache cache;
    private final MessageServiceImpl messageService;

    @Override
    public APIResultSet<TicketDetailDTO> createTicket(TicketDetailDTO dto) {
        try {
            APIResultSet<TicketDetailDTO> validationResult = validateTicketDTO(dto);
            if (validationResult.getHttpCode() != 200) {
                log.error(validationResult.getMessage());
                return validationResult;
            }

            if (dto.getFacebookUser() == null) {
                return  APIResultSet.badRequest("Lỗi thiếu Facebook User ID");
            }
            APIResultSet<Void> validateFacebookUser = facebookUserService.existsById(dto.getFacebookUser().getFacebookId());
            if (validateFacebookUser.getHttpCode() != 200) {
                return APIResultSet.badRequest(validateFacebookUser.getMessage());
            }

            Ticket ticket = mapper.toEntity(dto);
            if (ticket.getCreatedAt() == null) {
                ticket.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            }
            Ticket saved = ticketRepository.save(ticket);
            APIResultSet<TicketDetailDTO> result = APIResultSet.ok("Created successfully", mapper.toDetailDTO(saved));
            //update cache
            cache.putTicket(saved);
            log.info(result.getMessage());
            return result;

        } catch (Exception e) {
            log.info(e.getMessage());
            return APIResultSet.internalError();
        }
    }

    @Override
    public APIResultSet<TicketDetailDTO> updateTicket(int id, TicketDetailDTO dto) {
        try {
            Optional<Ticket> existingOpt = ticketRepository.findByIdWithDetails(id);
            if (existingOpt.isEmpty()) {
                return APIResultSet.notFound("Lỗi ticket không tồn tại");
            }

            APIResultSet<TicketDetailDTO> validationResult = validateTicketDTO(dto);
            if (validationResult.getHttpCode() != 200) {
                return validationResult;
            }

            Ticket existing = existingOpt.get();
            mapper.mergeToEntity(dto, existing);
            //TODO: check if ticket closed
            if (existing.getProgressStatus().getId() == 3) {
                calculateKPI(existing);
            }
            existing.setLastUpdateAt(new Timestamp(System.currentTimeMillis()));
            Ticket saved = ticketRepository.save(existing);
            APIResultSet<TicketDetailDTO> result = APIResultSet.ok("Updated successfully", mapper.toDetailDTO(saved));
            //update cache
            cache.putTicket(saved);
            log.info(result.getMessage());
            return result;
        } catch (Exception e) {
            log.info("Loi khong the update Ticket {}", e.getStackTrace());
            return APIResultSet.internalError();
        }
    }

    @Override
    public APIResultSet<TicketDetailDTO> getTicketById(int id) {
        try {
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
        } catch (Exception e) {
            log.info(e.getMessage());
            return APIResultSet.internalError();
        }
    }

    @Override
    public APIResultSet<Void> addTagToTicket(int ticketId, int hashtagId) {
        //TODO:
        return null;
    }

    @Override
    public APIResultSet<Void> removeTagFromTicket(int ticketId, int tagId) {
        //TODO:
        return null;
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
    public APIResultSet<List<TicketListDTO>> findAllByFacebookUserId(String facebookId) {
        try {
            List<Ticket> tickets = ticketRepository.findAllByFacebookUser_FacebookId(facebookId);
            APIResultSet<List<TicketListDTO>> result = APIResultSet.ok("OK", tickets.stream().map(mapper::toListDTO).toList());
            log.info(result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("Lỗi server" , e);
            return APIResultSet.internalError();
        }
    }

    @Override
    public APIResultSet<Void> addNoteToTicket(int ticketId, NoteDTO noteDTO) {
        //TODO:
        return null;
    }

    @Override
    public APIResultSet<Void> deleteNoteFromTicket(int ticketId, int noteId) {
        //TODO:
        return null;
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
    public APIResultSet<PaginationResponse<TicketListDTO>> searchTickets(TicketSearchCriteria criteria, Pageable pageable) {
        try {
            log.info(criteria.toString());
            var spec = TicketSpecification.build(criteria);
            var page = ticketRepository.findAll(spec, pageable);
            List<TicketListDTO> dtoList = page.getContent().stream()
                    .map(mapper::toListDTO)
                    .collect(Collectors.toList());
            PaginationResponse<TicketListDTO> pagination = new PaginationResponse<>(
                    dtoList,
                    page.getNumber(),
                    page.getSize(),
                    page.getTotalElements(),
                    page.getTotalPages()
            );
            APIResultSet<PaginationResponse<TicketListDTO>> result = APIResultSet.ok("Tim ticket ok", pagination);
            log.info(result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("Lỗi search ticket", e);
            return APIResultSet.internalError("Không thể thực hiện tìm kiếm.");
        }
    }



    private APIResultSet<TicketDetailDTO> validateTicketDTO(TicketDetailDTO dto) {
        if (dto.getAssignee() != null &&
                cache.getEmployee(dto.getAssignee().getUsername()) == null) {
            return APIResultSet.badRequest("Lỗi Staff không tồn tại: " + dto.getAssignee());
        }

        if (dto.getProgressStatus() == null) {
            return APIResultSet.badRequest("Lỗi thiếu trình trạng xử lý");
        } else if (cache.getProgress(dto.getProgressStatus().getCode()) == null){
            return APIResultSet.badRequest("Lỗi tình trạng xử lý không tồn tại " + dto.getProgressStatus().getCode());
        }

        if (dto.getCategory() != null &&
                cache.getCategory(dto.getCategory().getCode()) == null) {
            return APIResultSet.badRequest("Lỗi mã phân loại không tồn tại " + dto.getCategory().getCode());
        }

        if (dto.getEmotion() != null &&
                cache.getEmotion(dto.getEmotion().getCode()) == null) {
            return APIResultSet.badRequest("Lỗi mã cảm xúc không tồn tại " + dto.getEmotion().getCode());
        }

        if (dto.getSatisfaction() != null &&
                cache.getSatisfaction(dto.getSatisfaction().getCode()) == null) {
            return APIResultSet.badRequest("Lỗi mã hài lòng không tồn tại " + dto.getSatisfaction().getCode());
        }

        if (dto.getTags() != null) {
            for (TagDTO tag : dto.getTags()) {
                if (cache.getTag(tag.getName()) == null) {
                    return APIResultSet.badRequest("Lỗi tag không tồn tại " + tag);
                }
            }
        }

        return APIResultSet.ok("Validation passed", null);
    }

    @Override
    public APIResultSet<Void> deleteById(int ticketId) {
        try {
            ticketRepository.deleteById(ticketId);
            cache.updateOpeningTickets();
            log.info("Delete ticket id {} OK", ticketId);
            cache.getDashboardTickets().remove(ticketId);
            return APIResultSet.ok(String.format("Delete ticket id %d OK", ticketId), null);
        }
        catch(Exception e) {
            return APIResultSet.internalError();
        }
    }



    @Override
    public APIResultSet<List<TicketDashboardDTO>> getForDashboard() {
        try {
            APIResultSet<List<TicketDashboardDTO>> result = APIResultSet.ok("Lay ticket cho dashboard thanh cong",
                    cache.getDashboardTickets().values().stream().map(mapper::toDashboardDTO).toList());
            log.info(result.getMessage());
            return result;
        } catch(Exception e) {
            log.error("LOi khong the fetch ticket cho dashboard", e);
            return APIResultSet.internalError();
        }
    }

    private void calculateKPI(Ticket existing) {
        existing.setClosedAt(new Timestamp(System.currentTimeMillis()));
        long firstResponseRate = 0;
        long overallResponseRate = 0;
        long resolutionRate = (existing.getClosedAt().getTime() - existing.getCreatedAt().getTime())/1000;

        List<MessageDTO> messageList = new ArrayList<>();
        APIResultSet<List<MessageDTO>> result;
        result = messageService.getMessagesByTicketId(existing.getId());
        if (result.isSuccess()) {
            messageList.addAll(result.getData());
        } else {
            log.info("Loi tinh kpi ticket {}", result.getMessage());
            return;
        }

        messageList.sort((o1, o2) -> o1.getId() - o2.getId());

        // Tính first response time
        log.info("bat dau tinh");
        for (int i = 0; i < messageList.size(); i++) {
            MessageDTO current = messageList.get(i);
            if (i==0 && current.isSenderEmployee()) {
                log.info("o day ne");
                break;
            }

            if (current.isSenderEmployee()) {
                // tìm tin nhắn trước đó là của khách hàng
                MessageDTO customerMessage = messageList.get(i-1);
                log.info(current.toString());
                log.info(customerMessage.toString());
                firstResponseRate = (current.getTimestamp().getTime() - customerMessage.getTimestamp().getTime()) / 1000;
                break;
            }
        }

        // Tính average response time giữa mỗi lần khách nhắn và nhân viên trả lời
        List<Long> responseTimes = new ArrayList<>();
        MessageDTO lastCustomerMsg = null;
        for(int i=0; i<messageList.size(); i++) {
            MessageDTO msg = messageList.get(i);
            if (!msg.isSenderEmployee()) {
                lastCustomerMsg = msg;
            } else if (lastCustomerMsg != null) {
                long respTime = (msg.getTimestamp().getTime() - lastCustomerMsg.getTimestamp().getTime())/1000;
                if (respTime > 0) {
                    responseTimes.add(respTime);
                }
                lastCustomerMsg = null;
            }
        }

        //neu tin nhan cuoi cung la cua khach, thi lay thoi gian dong ticket tinh avg response
        if (lastCustomerMsg != null) {
            long respTime = (existing.getClosedAt().getTime() - lastCustomerMsg.getTimestamp().getTime())/1000;
            if (respTime > 0) {
                responseTimes.add(respTime);
            }
        }
//        log.info("responseTimes: {}", responseTimes);
        overallResponseRate = responseTimes.isEmpty()
                ? 0
                : responseTimes.stream().mapToLong(Long::longValue).sum() / responseTimes.size();

        // Gán lại vào Ticket nếu bạn có field tương ứng
        existing.setFirstResponseRate(firstResponseRate);
        existing.setOverallResponseRate(overallResponseRate);
        existing.setResolutionRate(resolutionRate);
        log.info("Ticket {} closed, tinh KPI thanh cong: {} {} {}", existing.getId(), firstResponseRate,
                overallResponseRate,
                resolutionRate);
    }

    @Override
    public APIResultSet<List<TicketVolumeReportDTO>> searchTicketsForVolumeReport(Timestamp fromTime, Timestamp toTime) {
        try {
            List<TicketVolumeReportDTO> tickets = ticketRepository.findTicketsForHourlyReport(fromTime, toTime);
            APIResultSet<List<TicketVolumeReportDTO>> result = APIResultSet.ok("Lấy ticket cho report thành công", tickets);
            log.info(result.getMessage());
            return result;
        } catch (Exception e) {
            log.info("Lỗi không thể lấy ticket cho report", e);
            return APIResultSet.internalError();
        }
    }
}

