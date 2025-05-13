package com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.service;

import com.binhbkfx02295.cshelpdesk.common.cache.MasterDataCache;
import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.dto.SatisfactionDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.entity.Satisfaction;
import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.mapper.SatisfactionMapper;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class SatisfactionServiceImpl implements SatisfactionService {

    private final MasterDataCache cache;
    private final SatisfactionMapper mapper;

    @Override
    public APIResultSet<List<SatisfactionDTO>> getAllSatisfaction() {
        try {
            APIResultSet<List<SatisfactionDTO>> result = APIResultSet.ok("Lay tat ca Muc hai long thanh cong",
                    cache.getAllSatisfactions().values().stream().map(mapper::toDTO).toList());
            log.info(result.getMessage());
            return result;

        } catch (Exception e) {
            log.info(e.getMessage());
            return APIResultSet.internalError();
        }
    }
}
