package com.binhbkfx02295.cshelpdesk.ticket_management.performance.service;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee;
import com.binhbkfx02295.cshelpdesk.infrastructure.common.cache.MasterDataCache;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.openai.common.PromptBuilder;
import com.binhbkfx02295.cshelpdesk.openai.model.TicketEvaluateResult;
import com.binhbkfx02295.cshelpdesk.openai.service.GPTTicketServiceImpl;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.dto.PerformanceSummaryDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.dto.TicketAssessmentDetailDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.mapper.PerformanceMapper;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.mapper.TicketAssessmentMapper;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.model.Criteria;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.model.TicketAssessment;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.repository.CriteriaRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.repository.TicketAssessmentRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.util.DateTimeUtil;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketReportDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PerformanceServiceImpl implements PerformanceService {

    private final MasterDataCache cache;
    private final PerformanceMapper mapper;
    private final CriteriaRepository criteriaRepository;
    private final TicketAssessmentRepository ticketAssessmentRepository;
    private final CriteriaService criteriaService;
    private final GPTTicketServiceImpl gptTicketService;
    private final TicketService ticketService;
    private final PromptBuilder promptBuilder;
    private final TicketAssessmentMapper ticketAssessmentMapper;

    @Override
    public APIResultSet<PerformanceSummaryDTO> getMonthlyReport(String username, int month, ZoneId zone) {

        Employee employee = cache.getEmployee(username);
        APIResultSet<PerformanceSummaryDTO> result;
        Timestamp[] timestamps = DateTimeUtil.getStartEndOfMonth(month, zone);
        List<TicketAssessment> ticketAssessments = ticketAssessmentRepository.findEvaluatedByMonth(username, timestamps[0], timestamps[1]);

        //compute first
        double firstResponseTime=0;
        double avgResponseTime=0;
        double resolutionTime=0;
        for (TicketAssessment ticket: ticketAssessments) {
            firstResponseTime += ticket.getFirstResponseTime();
            avgResponseTime += ticket.getAvgResponseTime();
            resolutionTime += ticket.getResolutionTime();
        }
        if (!ticketAssessments.isEmpty()) {
            firstResponseTime /= ticketAssessments.size();
            avgResponseTime /= ticketAssessments.size();
            resolutionTime /= ticketAssessments.size();
        }

        //response rate
        long firstResponseCount=ticketAssessments.stream().filter(t -> t.getFirstResponseTime() < 10.00).count();
        long avgResponseCount=ticketAssessments.stream().filter(t -> t.getAvgResponseTime() < 30.00).count();


        PerformanceSummaryDTO report = mapper.toSummary(employee.getUsername(), employee.getName(), month, ticketAssessments,
                Map.of("name", "Phản hồi đầu (dưới 10s)",
                        "ref", "Ít nhất 80%",
                        "avg", String.format("%.02fs", firstResponseTime),
                        "count", firstResponseCount,
                        "total", ticketAssessments.size(),
                        "passed", (double) firstResponseCount / ticketAssessments.size() > 0.8),
                Map.of("name", "Phản hồi trung bình (dưới 30s)",
                        "ref", "Ít nhất 80%",
                        "avg", String.format("%.02fs", avgResponseTime),
                        "count", avgResponseCount,
                        "total", ticketAssessments.size(),
                        "passed", (double) avgResponseCount / ticketAssessments.size() > 0.8),
                Map.of("name", "Xử lý trung bình",
                        "ref", "- -",
                        "avg", DateTimeUtil.formatDuration(resolutionTime),
                        "count", ticketAssessments.size(),
                        "total", ticketAssessments.size(),
                        "passed", true), "Chưa có đánh giá");

//        String chatGPTsummary = gptTicketService.analyseStaff(report);
//        report.getSummary().setChatGPTsummary(chatGPTsummary);

        try {
            result = APIResultSet.ok("Tạo report thành công", report);
        } catch (Exception e) {
            log.error("Loi khong the tao Performance Report", e);
            result = APIResultSet.internalError();
        }
        log.info(result.getMessage());
        return result;
    }

    @Override
    public APIResultSet<PerformanceSummaryDTO> getChatGPTSummary(String username, int month, ZoneId zone) {
        APIResultSet<PerformanceSummaryDTO> result;
        PerformanceSummaryDTO report;
        try {
            result = getMonthlyReport(username, month, zone);
            report = result.getData();
            String chatGPTsummary = gptTicketService.analyseStaff(report);
            report.getSummary().setChatGPTsummary(chatGPTsummary);

        } catch (Exception e) {
            result = APIResultSet.internalError();
        }
        return result;
    }

    @Async
    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Ho_Chi_Minh")
    public void evaluateTicketsScheduled() {
        evaluateTickets();
    }

    @Override
    public APIResultSet<Void> evaluateTickets() {
        APIResultSet<Void> result;
        try {

            APIResultSet<List<TicketReportDTO>> ticketsResult = ticketService.getForEvaluation();
            if (!ticketsResult.isSuccess()) {
                throw new RuntimeException("Lỗi server khi lấy ticket");
            }

            List<TicketAssessment> evaluatedTickets = ticketAssessmentRepository.findAllByEvaluatedTrue();
            Set<Long> evaluatedTicketIds = evaluatedTickets.stream()
                    .map(TicketAssessment::getTicketId)
                    .collect(Collectors.toSet());

            List<TicketReportDTO> ticketsList = ticketsResult.getData().stream()
                    .filter(ticketReport -> !evaluatedTicketIds.contains((long)ticketReport.getId()))
                    .collect(Collectors.toList());

            int initialBatchSize = 20;
            int i = 0;

            List<Map<String, Object>> allCriteriaObj = getCriteriaList();

            while (i < ticketsList.size()) {
                int batchSize = initialBatchSize;
                int end = Math.min(i + batchSize, ticketsList.size());
                List<TicketReportDTO> slicedTickets = ticketsList.subList(i, end);
                List<Map<String, Object>> ticketsObj = toListResolvedTicket(slicedTickets);

                Map<String, Object> payload = new HashMap<>();
                payload.put("criterias", allCriteriaObj);
                payload.put("tickets", ticketsObj);

                String prompt = promptBuilder.buildBatchEvaluateTicket(payload);

                // Giảm batch size nếu prompt quá dài
                while (prompt.length() > 20000 && batchSize > 1) {
                    batchSize--;
                    end = Math.min(i + batchSize, ticketsList.size());
                    ticketsObj = toListResolvedTicket(ticketsList.subList(i, end));
                    payload.put("tickets", ticketsObj);
                    prompt = promptBuilder.buildBatchEvaluateTicket(payload);
                }

                if (batchSize == 1 && prompt.length() > 20000) {
                    break;
                }

                TicketEvaluateResult gptResult = gptTicketService.evaluateTicketByBatch(payload);

                Set<Long> criteriaIds = gptResult.getResult().stream()
                        .flatMap(t -> t.getFailedCriterias().stream())
                        .map(Long::valueOf)
                        .collect(Collectors.toSet());
                Map<Long, TicketEvaluateResult.EvaluatedTicket> mapTickets = gptResult.getResult().stream().collect(Collectors.toMap(
                        TicketEvaluateResult.EvaluatedTicket::getId, Function.identity()
                ));
                Map<Long, Criteria> criteriaMap = criteriaRepository.findAllById(criteriaIds)
                        .stream()
                        .collect(Collectors.toMap(Criteria::getId, Function.identity()));

                List<TicketAssessment> assessments = new ArrayList<>();
                for (TicketReportDTO ticket : slicedTickets) {
                    TicketEvaluateResult.EvaluatedTicket evaluatedTicket = mapTickets.get((long)ticket.getId());
                    TicketAssessment entity = new TicketAssessment();
                    if (evaluatedTicket == null) {
                        entity.setPassed(true);
                    } else {
                        entity.setPassed(false);
                        entity.setFailedCriterias(evaluatedTicket.getFailedCriterias().stream()
                                .map(Long::valueOf)
                                .map(criteriaMap::get)
                                .filter(Objects::nonNull)
                                .toList());
                        entity.setSummary(evaluatedTicket.getSummary());
                    }


                    entity.setTicketId(ticket.getId());
                    entity.setCreatedAt(new Timestamp(ticket.getCreatedAt()));
                    entity.setEvaluatedAssignee(ticket.getUsername());
                    entity.setEvaluated(true);
                    entity.setEvaluatedBy("System");
                    entity.setEvaluatedAt(new Timestamp(System.currentTimeMillis()));
                    ticketService.calculateKPI(ticket);
                    entity.setFirstResponseTime(ticket.getFirstResponseTime());
                    entity.setAvgResponseTime(ticket.getAvgResponseTime());
                    entity.setResolutionTime(ticket.getResolutionTime());
                    assessments.add(entity);
                }

                ticketAssessmentRepository.saveAll(assessments);

                i += batchSize;
            }
            result = APIResultSet.ok("Đánh giá tickets thành công", null);
        } catch (Exception e) {
            log.error("", e);
            result = APIResultSet.internalError();
        }
        log.info(result.getMessage());
        return result;

    }

    private List<Map<String, Object>> getCriteriaList() {
        return criteriaService.findAll().getData().stream()
                .map(criteriaDTO -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", criteriaDTO.getId());
                    map.put("code", criteriaDTO.getCode());
                    map.put("name", criteriaDTO.getName());
                    map.put("description", criteriaDTO.getDescription());
                    return map;
                })
                .toList();
    }

    private List<Map<String, Object>> toListResolvedTicket(List<TicketReportDTO> tickets) {

        return tickets.stream()
                .map(ticketReportDTO -> Map.of(
                        "id", ticketReportDTO.getId(),
                        "messages", ticketReportDTO.getMessages().stream()
                                .map(messageDTO -> Map.of(
                                        "senderEmployee", messageDTO.isSenderEmployee(),
                                        "text", messageDTO.getText(),
                                        "timestamp", messageDTO.getTimestamp()
                                ))
                                .toList()
                ))
                .toList();
    }

    public APIResultSet<String> buildPrompt() {
        APIResultSet<List<TicketReportDTO>> ticketsResult = ticketService.getForEvaluation();
        if (!ticketsResult.isSuccess()) {
            throw new RuntimeException("Lỗi server khi lấy ticket");
        }

        List<TicketAssessment> evaluatedTickets = ticketAssessmentRepository.findAllByEvaluatedTrue();

        Set<Long> evaluatedTicketIds = evaluatedTickets.stream()
                .map(TicketAssessment::getTicketId)
                .collect(Collectors.toSet());
        List<TicketReportDTO> ticketsList = ticketsResult.getData().stream()
                .filter(ticketReport -> !evaluatedTicketIds.contains((long)ticketReport.getId()))
                .collect(Collectors.toList());


        List<Map<String, Object>> ticketsObj = toListResolvedTicket(ticketsList);
        List<Map<String, Object>> criteriasObj = getCriteriaList();
        Map<String, Object> tongHop = new HashMap<>();
        tongHop.put("criterias", criteriasObj);
        tongHop.put("tickets", ticketsObj);
        String prompt = promptBuilder.buildBatchEvaluateTicket(tongHop);
        System.out.println(prompt);
        return APIResultSet.ok("OK", prompt);
    }

    @Override
    public APIResultSet<TicketAssessmentDetailDTO> getTicketAssessment(Long id) {
        APIResultSet<TicketAssessmentDetailDTO> result;
        try {
            Optional<TicketAssessment> ticketOtp = ticketAssessmentRepository.findByTicketId(id);
            result = ticketOtp
                    .map(ticketAssessment -> APIResultSet.ok("Tìm ticket thành công", ticketAssessmentMapper.toDetailDTO(ticketAssessment)))
                    .orElseGet(() -> APIResultSet.badRequest("Không tìm thấy ticket"));
        } catch (Exception e) {
            log.error("", e);
            result = APIResultSet.internalError();
        }
        return result;
    }

    @Override
    public APIResultSet<TicketAssessmentDetailDTO> updateTicketAssessment(Long id, TicketAssessmentDetailDTO dto) {
        APIResultSet<TicketAssessmentDetailDTO> result;
        try {
            Optional<TicketAssessment> ticketOtp = ticketAssessmentRepository.findByTicketId(id);
            result = ticketOtp
                    .map(ticketAssessment -> {
                        TicketAssessment saved = ticketAssessmentRepository.save(ticketAssessmentMapper.mergeToEntity(ticketOtp.get(), dto));
                        return APIResultSet.ok("Tìm ticket thành công", ticketAssessmentMapper.toDetailDTO(saved));
                    })
                    .orElseGet(() -> APIResultSet.badRequest("Không tìm thấy ticket"));
        } catch (Exception e) {
            log.error("", e);
            result = APIResultSet.internalError();
        }
        return result;
    }
}
