package com.binhbkfx02295.cshelpdesk.facebookuser.service;

import com.binhbkfx02295.cshelpdesk.facebookgraphapi.dto.FacebookUserProfileDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.dto.*;
import com.binhbkfx02295.cshelpdesk.facebookuser.entity.FacebookUser;
import com.binhbkfx02295.cshelpdesk.facebookuser.mapper.FacebookUserMapper;
import com.binhbkfx02295.cshelpdesk.facebookuser.repository.FacebookUserRepository;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.PaginationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

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
            if (user.getCreatedAt() == null) {
                user.setCreatedAt(Instant.now());
            }
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

            if (user.getCreatedAt() == null) {
                user.setCreatedAt(Instant.now());
            }
            APIResultSet<FacebookUserDetailDTO> result = APIResultSet.ok(MSG_SUCCESS, mapper.toDetailDTO(user));
            log.info(result.getMessage());
            return result;
        } catch (Exception e) {
            log.error(e.getMessage());
            return APIResultSet.internalError(e.getMessage());
        }
    }

    @Override
    public APIResultSet<FacebookUserDetailDTO> save(FacebookUserProfileDTO userDTO) {
        try {
            FacebookUser user = facebookUserRepository.save(mapper.toEntity(userDTO));

            if (user.getCreatedAt() == null) {
                user.setCreatedAt(Instant.now());
            }
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
            FacebookUser entity = facebookUserRepository.get(updatedUserDTO.getFacebookId());

            return Optional.ofNullable(facebookUserRepository.update(mapper.toEntity(updatedUserDTO)))
                    .map(user -> {
                        APIResultSet<FacebookUserDetailDTO> result = APIResultSet.ok(String.format("Cập nhật khách hàng ID: %s thành công.", updatedUserDTO.getFacebookId()), mapper.toDetailDTO(user));
                        log.info(result.getMessage());
                        return result;
                    })
                    .orElseGet(() -> APIResultSet.notFound(MSG_ERROR_USER_NOT_FOUND));
        } catch (Exception e) {
            e.printStackTrace();
            log.info(String.valueOf(e));
            return APIResultSet.internalError(MSG_ERROR_INTERNAL_SERVER_ERROR);
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
            if (s == null || s.isEmpty() || s.isBlank()) {
                APIResultSet<Void> result = APIResultSet.badRequest("Facebook ID bị thiếu");
                log.info(result.getMessage());
                return result;
            }

            if (!facebookUserRepository.existsById(s)) {
                APIResultSet<Void> result = APIResultSet.badRequest(("Facebook ID không tồn tại"));
                log.info(result.getMessage());
                return result;
            }
            facebookUserRepository.deleteById(s);
            log.info("Xóa Khách hàng ID: {} thành công.", s);
            return APIResultSet.ok(String.format("Xóa Khách hàng ID: %s thành công.", s), null);
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

    @Override
    public APIResultSet<Void> deleteAll(ArrayList<String> ids) {
        try {
            facebookUserRepository.deleteAll(ids);
            APIResultSet<Void> result = APIResultSet.ok("Xoa nhom id thanh cong", null);
            log.info(result.getMessage());
            return result;
        } catch (Exception e) {
            log.info(Arrays.toString(e.getStackTrace()));
            return APIResultSet.internalError("Loi Xoa nhom id");
        }
    }



    public List<FacebookUserExportDTO> exportSearchUsers(FacebookUserSearchCriteria criteria, Pageable pageable) {
        try {
            Map<String, Object> queryResult = facebookUserRepository.search(criteria, pageable);
            List<FacebookUserExportDTO> content = ((List<FacebookUser>)queryResult.get("content")).stream().map(mapper::toExportDTO).toList();
            return content;
        } catch (Exception e) {
            log.info(Arrays.toString(e.getStackTrace()));
            return null;
        }
    }


}
