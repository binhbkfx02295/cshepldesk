package com.binhbkfx02295.cshelpdesk.facebookuser.repository.dao;

import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserSearchCriteria;
import com.binhbkfx02295.cshelpdesk.facebookuser.entity.FacebookUser;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface FacebookUserDAO {

    FacebookUser save(FacebookUser user);

    FacebookUser get(String id);

    List<FacebookUser> search(String fullName);

    Map<String, Object> search(FacebookUserSearchCriteria criteria, Pageable page);

    List<FacebookUser> getAll();

    FacebookUser update(FacebookUser updatedUser);

    boolean existsById(String id);

    void deleteById(String facebookUserId);

    void deleteAll(List<String> ids);

    FacebookUser getReferenceById(String facebookId);
}
