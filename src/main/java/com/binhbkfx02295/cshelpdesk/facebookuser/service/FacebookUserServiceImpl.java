package com.binhbkfx02295.cshelpdesk.facebookuser.service;

import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.entity.FacebookUser;
import com.binhbkfx02295.cshelpdesk.facebookuser.repository.FacebookUserRepository;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FacebookUserServiceImpl implements FacebookUserService {

    private FacebookUserRepository facebookUserRepository;

    @Autowired
    public FacebookUserServiceImpl(FacebookUserRepository repository) {
        facebookUserRepository = repository;
    }

    @Override
    public APIResultSet save(FacebookUser user) {
        try {
            facebookUserRepository.save(user);
            return APIResultSet.ok(MSG_SUCCESS, null);
        } catch (Exception e) {
            return APIResultSet.internalError(e.getMessage());
        }
    }

    @Override
    public APIResultSet<FacebookUserDTO> update(FacebookUser updatedUser) {
        try {
            return Optional.ofNullable(facebookUserRepository.update(updatedUser))
                    .map(result -> APIResultSet.ok(MSG_SUCCESS, new FacebookUserDTO(result)))
                    .orElseGet(() -> APIResultSet.notFound(MSG_ERROR_USER_NOT_FOUND));
        } catch (Exception e) {
            return APIResultSet.internalError(MSG_ERROR_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public APIResultSet<List<FacebookUserDTO>> search(String name) {
        try {
            return APIResultSet.ok(MSG_SUCCESS, facebookUserRepository.search(name).stream().map(FacebookUserDTO::new).toList());
        } catch (Exception e) {
            log.error(e.getMessage());
            return APIResultSet.internalError(e.getMessage());
        }
    }

    @Override
    public APIResultSet<FacebookUserDTO> get(String id) {
        try {
            return Optional.ofNullable(facebookUserRepository.get(id))
                    .map(user -> APIResultSet.ok(MSG_SUCCESS, new FacebookUserDTO(user)))
                    .orElseGet(() -> APIResultSet.notFound(MSG_ERROR_USER_NOT_FOUND));
        } catch (Exception e) {
            log.error(e.getMessage());
            return APIResultSet.internalError(MSG_ERROR_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public APIResultSet<List<FacebookUserDTO>> getAll() {
        try {
            return Optional.ofNullable(facebookUserRepository.getAll())
                    .map(users -> APIResultSet.ok(MSG_SUCCESS, users.stream().map(FacebookUserDTO::new).toList()))
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
            return APIResultSet.notFound("Facebook User không tồn tại");
        } catch (Exception e) {
            log.error("Lỗi không thể kiểm tra facebook user tồn tại ", e);
            return APIResultSet.notFound("Lỗi không thể kiểm tra facebook user tồn tại");
        }
    }
}
