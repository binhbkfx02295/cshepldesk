package com.binhbkfx02295.cshelpdesk.facebookuser.spec;

import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserSearchCriteria;
import com.binhbkfx02295.cshelpdesk.facebookuser.entity.FacebookUser;
import org.springframework.data.jpa.domain.Specification;

public class FacebookUserSpecification {

    public static Specification<FacebookUser> build(FacebookUserSearchCriteria criteria) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (criteria.getFacebookId() != null && !criteria.getFacebookId().isBlank())
                predicate = cb.and(predicate, cb.like(root.get("facebookId"), "%" + criteria.getFacebookId() + "%"));

            if (criteria.getFacebookName() != null && !criteria.getFacebookName().isBlank())
                predicate = cb.and(predicate, cb.like(root.get("facebookName"), "%" + criteria.getFacebookName() + "%"));

            if (criteria.getRealName() != null && !criteria.getRealName().isBlank())
                predicate = cb.and(predicate, cb.like(root.get("realName"), "%" + criteria.getRealName() + "%"));

            if (criteria.getEmail() != null && !criteria.getEmail().isBlank())
                predicate = cb.and(predicate, cb.like(root.get("email"), "%" + criteria.getEmail() + "%"));

            if (criteria.getPhone() != null && !criteria.getPhone().isBlank())
                predicate = cb.and(predicate, cb.like(root.get("phone"), "%" + criteria.getPhone() + "%"));

            if (criteria.getZalo() != null && !criteria.getZalo().isBlank())
                predicate = cb.and(predicate, cb.like(root.get("zalo"), "%" + criteria.getZalo() + "%"));

            return predicate;
        };
    }
}
