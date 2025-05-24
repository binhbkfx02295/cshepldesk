package com.binhbkfx02295.cshelpdesk.ticket_management.emotion.service;

import com.binhbkfx02295.cshelpdesk.infrastructure.common.cache.MasterDataCache;
import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.dto.EmotionDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.mapper.EmotionMapper;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class EmotionServiceImpl implements EmotionService{

    private final MasterDataCache cache;
    private final EmotionMapper mapper;

    @Override
    public APIResultSet<List<EmotionDTO>> getAllEmotion() {
        try {
            APIResultSet<List<EmotionDTO>> result = APIResultSet.ok("Lay all emotions thanh cong", cache.getAllEmotions().values()
                    .stream().map(mapper::toDTO).toList());
            log.info(result.getMessage());
            return result;
        } catch (Exception e) {
            log.info(e.getMessage());
            return APIResultSet.internalError();
        }
    }
}
