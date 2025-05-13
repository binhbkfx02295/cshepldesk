package com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.service;

import com.binhbkfx02295.cshelpdesk.common.cache.MasterDataCache;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.dto.ProgressStatusDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.entity.ProgressStatus;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.mapper.ProgressStatusMapper;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.repository.ProgressStatusRepository;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProgressStatusServiceImpl implements ProgressStatusService {

    private final ProgressStatusRepository repository;
    private final ProgressStatusMapper mapper;
    private final MasterDataCache cache;

    @Override
    public APIResultSet<List<ProgressStatusDTO>> getAllProgressStatus() {
        try {
            APIResultSet<List<ProgressStatusDTO>> result = APIResultSet.ok("Lay all progress status thanh cong",
                    cache.getAllProgress().values().stream().map(mapper::toDTO).toList());
            log.info(result.getMessage());
            return result;
        } catch (Exception e) {
            log.info(e.getMessage());
            return APIResultSet.internalError();
        }
    }




}
