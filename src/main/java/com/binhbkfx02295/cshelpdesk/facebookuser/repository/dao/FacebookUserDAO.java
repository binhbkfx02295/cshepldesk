package com.binhbkfx02295.cshelpdesk.facebookuser.repository.dao;

import com.binhbkfx02295.cshelpdesk.facebookuser.entity.FacebookUser;

import java.util.List;

public interface FacebookUserDAO {

    FacebookUser save(FacebookUser user);

    FacebookUser get(String id);

    List<FacebookUser> search(String fullName);

    List<FacebookUser> getAll();

    FacebookUser update(FacebookUser updatedUser);

    boolean existsById(String id);

    void deleteById(String facebookUserId);
}
