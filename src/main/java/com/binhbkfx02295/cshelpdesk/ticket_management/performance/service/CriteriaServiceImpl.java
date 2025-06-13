package com.binhbkfx02295.cshelpdesk.ticket_management.performance.service;

import com.binhbkfx02295.cshelpdesk.infrastructure.common.cache.MasterDataCache;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.dto.CriteriaDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.dto.CriteriaDetailDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.mapper.CriteriaMapper;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.model.Criteria;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.repository.CriteriaRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CriteriaServiceImpl implements CriteriaService {

    private final CriteriaMapper mapper;
    private final CriteriaRepository repository;
    private final MasterDataCache cache;
    private final EntityManager entityManager;

    @Override
    public APIResultSet<List<CriteriaDTO>> findAll() {
        APIResultSet<List<CriteriaDTO>> result;
        try {
            result = APIResultSet.ok(
                    FIND_ALL_SUCCESS,
                    cache.getAllCriterias().values().stream().map(mapper::toDTO).toList());
        } catch (Exception e) {
            log.error("Lỗi server", e);
            result = APIResultSet.internalError();
        }
        return result;
    }

    @Override
    public APIResultSet<CriteriaDetailDTO> findById(Long id) {
        APIResultSet<CriteriaDetailDTO> result;
        Optional<Criteria> exists = repository.findById(id);
        try {
            result = exists.map(criteria -> APIResultSet.ok(
                    FIND_SUCCESS, mapper.toDetailDTO(criteria)
            )).orElseGet(() -> APIResultSet.badRequest(EXISTS_FALSE));

        } catch (Exception e) {
            log.error("Lỗi server", e);
            result = APIResultSet.internalError();
        }
        return result;
    }

    @Override
    public APIResultSet<CriteriaDetailDTO> create(CriteriaDetailDTO dto) {
        APIResultSet<CriteriaDetailDTO> result;
        try {
            dto.setActive(true);
            Criteria saved = repository.save(mapper.toEntity(dto));
            entityManager.flush();
            entityManager.clear();
            cache.updateCriteria(saved);
            result = APIResultSet.ok(
                    CREATE_SUCCESS,
                    mapper.toDetailDTO(saved));
        } catch (Exception e) {
            log.error("Lỗi server", e);
            result = APIResultSet.internalError();
        }
        return result;
    }

    @Override
    public APIResultSet<CriteriaDetailDTO> update(Long id, CriteriaDetailDTO dto) {
        APIResultSet<CriteriaDetailDTO> result;
        try {
            Optional<Criteria> criteriaOtp = repository.findById(id);
            if (criteriaOtp.isPresent()) {
                Criteria criteria = criteriaOtp.get();
                mapper.mergeToEntity(criteria, dto);
                Criteria saved = repository.save(criteria);

                entityManager.flush();
                entityManager.clear();
                if (saved.isActive()) {
                    cache.updateCriteria(saved);
                } else {
                    cache.getAllCriterias().values().remove(id);
                }
                result = APIResultSet.ok(UPDATE_OK, mapper.toDetailDTO(saved));
            } else {
                result = APIResultSet.notFound(EXISTS_FALSE);
            }
        } catch (Exception e) {
            log.error("Lỗi server", e);
            result = APIResultSet.internalError();
        }
        return result;
    }

    @Override
    public APIResultSet<Void> delete(Long id) {
        APIResultSet<Void> result;
        try {
            boolean existed = cache.getCriteria(id) != null;
            if (!existed) {
                result = APIResultSet.notFound(EXISTS_FALSE);
            } else {
                repository.deleteById(id);
                cache.getAllCriterias().remove(id);
                result = APIResultSet.ok(DELETE_OK, null);
            }
        } catch (Exception e) {
            log.error("Lỗi server", e);
            result = APIResultSet.internalError();
        }
        return result;
    }

    @Override
    public APIResultSet<Void> existsById(Long id) {
        APIResultSet<Void> result;
        try {
            boolean existed = cache.getCriteria(id) != null;
            if (!existed) {
                result = APIResultSet.notFound(EXISTS_FALSE);
            } else {
                result = APIResultSet.ok(EXISTS_TRUE, null);
            }
        } catch (Exception e) {
            log.error("Lỗi server", e);
            result = APIResultSet.internalError();
        }
        return result;
    }
}
