package com.binhbkfx02295.cshelpdesk.facebookuser.service;

import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserListDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserDetailDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserFetchDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserSearchCriteria;
import com.binhbkfx02295.cshelpdesk.facebookuser.entity.FacebookUser;
import com.binhbkfx02295.cshelpdesk.facebookuser.mapper.FacebookUserMapper;
import com.binhbkfx02295.cshelpdesk.facebookuser.repository.FacebookUserRepository;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.util.PaginationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FacebookUserServiceImpl implements FacebookUserService {
    private final FacebookUserRepository facebookUserRepository;
    private final FacebookUserMapper mapper;

    @Override
    public APIResultSet<FacebookUserDetailDTO> save(FacebookUserDetailDTO userDTO) {
        try {
            FacebookUser user = facebookUserRepository.save(mapper.toEntity(userDTO));
            APIResultSet<FacebookUserDetailDTO> result = APIResultSet.ok(MSG_SUCCESS, mapper.toDetailDTO(user));
            log.info(result.getMessage());
            return result;
        } catch (Exception e) {
            log.error(e.getMessage());
            return APIResultSet.internalError(e.getMessage());
        }
    }

    @Override
    public APIResultSet<FacebookUserDetailDTO> save(FacebookUserFetchDTO userDTO) {
        try {
            FacebookUser user = facebookUserRepository.save(mapper.toEntity(userDTO));
            APIResultSet<FacebookUserDetailDTO> result = APIResultSet.ok(MSG_SUCCESS, mapper.toDetailDTO(user));
            log.info(result.getMessage());
            return result;
        } catch (Exception e) {
            log.error(e.getMessage());
            return APIResultSet.internalError(e.getMessage());
        }
    }

    @Override
    public APIResultSet<FacebookUserDetailDTO> update(FacebookUserDetailDTO updatedUserDTO) {
        try {
            return Optional.ofNullable(facebookUserRepository.update(mapper.toEntity(updatedUserDTO)))
                    .map(user -> {
                        APIResultSet<FacebookUserDetailDTO> result = APIResultSet.ok(MSG_SUCCESS, mapper.toDetailDTO(user));
                        log.info(result.getMessage());
                        return result;
                    })
                    .orElseGet(() -> APIResultSet.notFound(MSG_ERROR_USER_NOT_FOUND));
        } catch (Exception e) {
            return APIResultSet.internalError(MSG_ERROR_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public APIResultSet<List<FacebookUserListDTO>> search(String name) {
        try {
            return APIResultSet.ok(MSG_SUCCESS, facebookUserRepository.search(name).stream().map(mapper::toListDTO).toList());
        } catch (Exception e) {
            log.error(e.getMessage());
            return APIResultSet.internalError(e.getMessage());
        }
    }

    @Override
    public APIResultSet<FacebookUserDetailDTO> get(String id) {
        try {
            return Optional.ofNullable(facebookUserRepository.get(id))
                    .map(user -> {
                        APIResultSet<FacebookUserDetailDTO> result = APIResultSet.ok(MSG_SUCCESS, mapper.toDetailDTO(user));
                        log.info(result.getMessage());
                        return result;
                    })
                    .orElseGet(() -> APIResultSet.notFound(MSG_ERROR_USER_NOT_FOUND));
        } catch (Exception e) {
            log.error(e.getMessage());
            return APIResultSet.internalError(MSG_ERROR_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public APIResultSet<List<FacebookUserListDTO>> getAll() {
        try {
            return Optional.ofNullable(facebookUserRepository.getAll())
                    .map(users -> APIResultSet.ok(MSG_SUCCESS, users.stream().map(mapper::toListDTO).toList()))
                    .orElseGet(() -> APIResultSet.notFound(MSG_ERROR_SEARCH_NOT_FOUND));

        } catch (Exception e) {
            log.error(e.getMessage());
            return APIResultSet.internalError(e.getMessage());
        }
    }

    @Override
    public APIResultSet<Void> existsById(String facebookId) {
        try {
            if (facebookUserRepository.existsById(facebookId))
                return APIResultSet.ok("Facebook User có tồn tại", null);
            log.info("Facebook User không ton tai");
            return APIResultSet.notFound("Facebook User không tồn tại");
        } catch (Exception e) {
            log.error("Lỗi không thể kiểm tra facebook user tồn tại ", e);
            return APIResultSet.notFound("Lỗi không thể kiểm tra facebook user tồn tại");
        }
    }

    @Override
    public APIResultSet<Void> deleteById(String s) {
        try {
            facebookUserRepository.deleteById(s);
            log.info("Delete facebookuser {} OK", s);
            return APIResultSet.ok();
        } catch (Exception e) {
            log.error(e.getMessage());
            return APIResultSet.internalError();
        }
    }

    @Override
    public APIResultSet<PaginationResponse<FacebookUserDetailDTO>> searchUsers(FacebookUserSearchCriteria criteria, Pageable pageable) {
        try {
            Map<String, Object> queryResult = facebookUserRepository.search(criteria, pageable);
            List<FacebookUserDetailDTO> content = ((List<FacebookUser>)queryResult.get("content")).stream().map(mapper::toDetailDTO).toList();
            PaginationResponse<FacebookUserDetailDTO> response = new PaginationResponse<>();
            response.setContent(content);
            response.setTotalElements((long)queryResult.get("totalElements"));
            response.setPage(pageable.getPageNumber());
            response.setSize(pageable.getPageSize());
            response.setTotalPages((int) Math.ceil((double) response.getTotalElements() / response.getSize()));
            return APIResultSet.ok("search facebook user thanh cong", response);
        } catch (Exception e) {
            log.info(Arrays.toString(e.getStackTrace()));
            return APIResultSet.internalError();
        }
    }

}
