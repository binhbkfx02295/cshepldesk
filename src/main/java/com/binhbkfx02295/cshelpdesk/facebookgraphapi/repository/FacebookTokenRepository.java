package com.binhbkfx02295.cshelpdesk.facebookgraphapi.repository;

import com.binhbkfx02295.cshelpdesk.facebookgraphapi.entity.FacebookToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacebookTokenRepository extends JpaRepository<FacebookToken, Integer> {
    Optional<FacebookToken> findFirstByPageIdOrderByLastUpdatedDesc(String pageId);
}
