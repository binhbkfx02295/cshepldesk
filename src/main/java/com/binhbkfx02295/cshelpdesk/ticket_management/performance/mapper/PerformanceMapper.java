package com.binhbkfx02295.cshelpdesk.ticket_management.performance.mapper;

import com.binhbkfx02295.cshelpdesk.infrastructure.common.cache.MasterDataCache;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.dto.*;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.model.Criteria;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.model.TicketAssessment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PerformanceMapper {

    private final TicketAssessmentMapper mapper;
    private final MasterDataCache cache;

    public PerformanceSummaryDTO toSummary(
            String username,
            String displayName,
            int month,
            List<TicketAssessment> list,
            Map<String, Object> firstRT,
            Map<String, Object> avgRT,
            Map<String, Object> resolutionRT,
            String chatGPTSummary) {

        long total   = list.size();
        long failed  = list.stream().filter(ticket -> !ticket.isPassed()).count();
        double rate  = total == 0 ? 0 : (double) failed / total;

        Map<Long, Long> byCrit = list.stream()
                .flatMap(ticket -> ticket.getFailedCriterias().stream())
                .collect(Collectors.groupingBy(Criteria::getId, Collectors.counting()));

        List<FailedCriteriaStatDTO> critStats = byCrit.entrySet().stream()
                .map(e -> {
                    Criteria criteria = cache.getCriteria(e.getKey());
                    FailedCriteriaStatDTO failedCriteriaStatDTO = new FailedCriteriaStatDTO();
                    failedCriteriaStatDTO.setCode(criteria.getCode());
                    failedCriteriaStatDTO.setDescription(criteria.getDescription());
                    failedCriteriaStatDTO.setName(criteria.getName());
                    failedCriteriaStatDTO.setCount(e.getValue());
                    return failedCriteriaStatDTO;
                })
                .sorted(Comparator.comparingLong(FailedCriteriaStatDTO::getCount).reversed())
                .toList();

        List<TicketAssessmentListDTO> ticketList =
                list.stream().map(mapper::toListDTO).toList();

        PerformanceSummaryStatDTO statDTO = new PerformanceSummaryStatDTO(
                Map.of(
                        "name", "Tỷ lệ lỗi trò chuyện",
                        "ref", "Nhiều nhất 20%",
                        "avg", String.format("%.2f%%", rate*100),
                        "count", failed,
                        "total", list.size(),
                        "passed", rate < 0.2,
                        "failedCriterias", critStats,
                        "ticketList", ticketList
                ),
                firstRT, avgRT, resolutionRT,
                chatGPTSummary);



        return new PerformanceSummaryDTO(
                new AssigneeDTO(username, displayName),
                month,
                statDTO
        );
    }
}
