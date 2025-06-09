package com.binhbkfx02295.cshelpdesk.ticket_management.ticket.service;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee;
import com.binhbkfx02295.cshelpdesk.infrastructure.common.cache.MasterDataCache;
import com.binhbkfx02295.cshelpdesk.facebookuser.service.FacebookUserServiceImpl;
import com.binhbkfx02295.cshelpdesk.message.dto.MessageDTO;
import com.binhbkfx02295.cshelpdesk.message.entity.Message;
import com.binhbkfx02295.cshelpdesk.message.service.MessageServiceImpl;
import com.binhbkfx02295.cshelpdesk.openai.model.GPTResult;
import com.binhbkfx02295.cshelpdesk.openai.service.GPTTicketService;
import com.binhbkfx02295.cshelpdesk.ticket_management.category.repository.CategoryRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.repository.EmotionRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.note.entity.Note;
import com.binhbkfx02295.cshelpdesk.ticket_management.note.dto.NoteDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.note.repository.NoteRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.repository.SatisfactionRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.tag.dto.TagDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.*;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.repository.TicketRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.mapper.TicketMapper;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.spec.TicketSpecification;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.PaginationResponse;
import com.binhbkfx02295.cshelpdesk.websocket.event.TicketAssignedEvent;
import com.binhbkfx02295.cshelpdesk.websocket.event.TicketEvent;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
    private final TicketMapper mapper;
    private final NoteRepository noteRepository;
    private final FacebookUserServiceImpl facebookUserService;
    private final MasterDataCache cache;
    private final MessageServiceImpl messageService;
    private final GPTTicketService gptService;
    private final CategoryRepository categoryRepository;
    private final EmotionRepository emotionRepository;
    private final SatisfactionRepository satisfactionRepository;
    private final ApplicationEventPublisher publisher;
    private final EntityManager entityManager;


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
            //update cache
            entityManager.flush();
            entityManager.clear();
            cache.updateOpeningTickets();
            log.info("Kiem tra id cua ticket saved: {}", saved.getId());
            log.info("kiem tra map to detail DTO cua ticket: {}", mapper.toDetailDTO(cache.getTicket(saved.getId())));
            APIResultSet<TicketDetailDTO> result = APIResultSet.ok("Created successfully", mapper.toDetailDTO(cache.getTicket(saved.getId())));
            log.info(result.getMessage());
            //TODO: publish event
            publisher.publishEvent(new TicketEvent(mapper.toDashboardDTO(cache.getTicket(saved.getId())), TicketEvent.Action.CREATED));
            return result;

        } catch (Exception e) {
            log.info("Loi khi tao moi ticket", e);
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

            if (existing.getProgressStatus().getId() == 3 &&
                    (existing.getFirstResponseRate() == null ||
                            existing.getOverallResponseRate() == null ||
                            existing.getResolutionRate() == null)) {
                existing.setClosedAt(new Timestamp(System.currentTimeMillis()));
                calculateKPI(existing);

                //TODO: goi analyse service
                List<Message> messages = cache.getAllMessages().values().stream().filter(message ->
                        message.getTicket().getId() == existing.getId()).toList();
                if (!messages.isEmpty()) {
                    messages = existing.getMessages();
                }
                GPTResult gptResult = gptService.analyze(messages);

                existing.setCategory(categoryRepository.getReferenceById(gptResult.getCategoryId()));
                existing.setSatisfaction(satisfactionRepository.getReferenceById(gptResult.getSatisfactionId()));
                existing.setEmotion(emotionRepository.getReferenceById(gptResult.getEmotionId()));
                existing.setPrice(gptResult.getPrice());
                existing.setGptModelUsed(gptResult.getGptModelused());
                existing.setTokenUsed(gptResult.getTokenUsed());

            }
            existing.setLastUpdateAt(new Timestamp(System.currentTimeMillis()));
            Ticket saved = ticketRepository.save(existing);
            //update cache
            entityManager.flush();
            entityManager.clear();
            cache.updateOpeningTickets();
            //TODO: publish event
            if (saved.getProgressStatus().getId() == 3) {
                Ticket temp = cache.getTicket(existing.getId());
                if (temp == null) {
                    temp = ticketRepository.findByIdWithDetails(existing.getId()).get();
                }

                publisher.publishEvent(new TicketEvent(mapper.toDashboardDTO(temp), TicketEvent.Action.CLOSED));
            }
            else {
                publisher.publishEvent(new TicketEvent(mapper.toDashboardDTO(cache.getTicket(existing.getId())), TicketEvent.Action.UPDATED));
            }

            Ticket temp = cache.getTicket(existing.getId());


            if (temp == null) {
                temp = ticketRepository.findByIdWithDetails(existing.getId()).get();
            }
            //return result
            APIResultSet<TicketDetailDTO> result = APIResultSet.ok("Updated successfully", mapper.toDetailDTO(temp));
            log.info(result.getMessage());
            return result;
        } catch (Exception e) {
            log.info("Loi khong the update Ticket ", e);
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
    public APIResultSet<TicketDetailDTO> findExistingTicket(String facebookId) {
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
        } else if (cache.getProgress(dto.getProgressStatus().getId()) == null){
            return APIResultSet.badRequest("Lỗi tình trạng xử lý không tồn tại " + dto.getProgressStatus().getId());
        }

        if (dto.getCategory() != null &&
                cache.getCategory(dto.getCategory().getId()) == null) {
            return APIResultSet.badRequest("Lỗi mã phân loại không tồn tại " + dto.getCategory().getId());
        }

        if (dto.getEmotion() != null &&
                cache.getEmotion(dto.getEmotion().getId()) == null) {
            return APIResultSet.badRequest("Lỗi mã cảm xúc không tồn tại " + dto.getEmotion().getId());
        }

        if (dto.getSatisfaction() != null &&
                cache.getSatisfaction(dto.getSatisfaction().getId()) == null) {
            return APIResultSet.badRequest("Lỗi mã hài lòng không tồn tại " + dto.getSatisfaction().getCode());
        }

        if (dto.getTags() != null) {
            for (TagDTO tag : dto.getTags()) {
                if (cache.getTag(tag.getId()) == null) {
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
    public APIResultSet<List<TicketDashboardDTO>> getForDashboard(String username) {
        try {
            List<Ticket> tickets = cache.getDashboardTickets().values().stream().toList();
            Employee employee = cache.getEmployee(username);

            //TODO: filter tickets assigned only to the staff
            if (employee != null && employee.getUserGroup().getCode().equalsIgnoreCase("staff")) {
                tickets = tickets.stream().filter(ticket ->
                        ticket.getAssignee() == null || ticket.getAssignee().getUsername().equalsIgnoreCase(username)).toList();
            }

            APIResultSet<List<TicketDashboardDTO>> result = APIResultSet.ok("Lay ticket cho dashboard thanh cong",
                    tickets.stream().map(mapper::toDashboardDTO).toList());

            log.info(result.getMessage());
            return result;
        } catch(Exception e) {
            log.error("LOi khong the fetch ticket cho dashboard", e);
            return APIResultSet.internalError();
        }
    }

    private void calculateKPI(Ticket existing) {

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
        for (MessageDTO msg : messageList) {
            if (!msg.isSenderEmployee()) {
                lastCustomerMsg = msg;
            } else if (lastCustomerMsg != null) {
                long respTime = (msg.getTimestamp().getTime() - lastCustomerMsg.getTimestamp().getTime()) / 1000;
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
            APIResultSet<List<TicketVolumeReportDTO>> result = APIResultSet.ok(String.format("Lấy ticket cho report thành công, tổng cộng %s", tickets.size()), tickets);
            log.info(result.getMessage());
            return result;
        } catch (Exception e) {
            log.info("Lỗi không thể lấy ticket cho report", e);
            return APIResultSet.internalError();
        }
    }

    @Override
    public APIResultSet<TicketDetailDTO> assignTicket(int id, TicketDetailDTO dto) {
        APIResultSet<TicketDetailDTO> result;
        try {
            if (dto.getAssignee() == null) {
                result = APIResultSet.badRequest("Lỗi chưa gán assignee");
            } else {
                result = updateTicket(id, dto);
                publisher.publishEvent(new TicketAssignedEvent(mapper.toDashboardDTO(cache.getTicket(id))));
            }
        } catch (Exception e) {
            log.error("TicketServiceImpl.assignTicket: loi server", e);
            result = APIResultSet.internalError("Lỗi không thể assign ticket");
        }
        log.info(result.getMessage());
        return result;
    }


}

