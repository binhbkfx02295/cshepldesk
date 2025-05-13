package com.binhbkfx02295.cshelpdesk.ticket_management.ticket.spec;

import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketSearchCriteria;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;


import java.util.ArrayList;
import java.util.List;

public class TicketSpecification  {

    public static Specification<Ticket> build(TicketSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getAssignee() != null) {
                predicates.add(cb.equal(root.get("assignee").get("username"), criteria.getAssignee()));
            }
            if (criteria.getFacebookUserId() != null) {
                predicates.add(cb.equal(root.get("facebookUser").get("facebookId"), criteria.getFacebookUserId()));
            }
            if (criteria.getTitle() != null) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + criteria.getTitle().toLowerCase() + "%"));
            }
            if (criteria.getProgressStatus() != null) {
                predicates.add(cb.equal(root.get("progressStatus").get("code"), criteria.getProgressStatus()));
            }
            if (criteria.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), criteria.getFromDate()));
            }
            if (criteria.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), criteria.getToDate()));
            }
            if (criteria.getCategory() != null) {
                predicates.add(cb.equal(root.get("category").get("code"), criteria.getCategory()));
            }
            if (criteria.getEmotion() != null) {
                predicates.add(cb.equal(root.get("emotion").get("code"), criteria.getEmotion()));
            }
            if (criteria.getSatisfaction() != null) {
                predicates.add(cb.equal(root.get("satisfaction").get("code"), criteria.getSatisfaction()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
