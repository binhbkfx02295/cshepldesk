package com.binhbkfx02295.cshelpdesk.facebookuser.repository;

import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserSearchCriteria;
import com.binhbkfx02295.cshelpdesk.facebookuser.entity.FacebookUser;
import com.binhbkfx02295.cshelpdesk.facebookuser.repository.dao.FacebookUserDAO;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
@Transactional
@Slf4j
public class FacebookUserRepository implements FacebookUserDAO {

    private final EntityManager entityManager;

    @Autowired
    public FacebookUserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public FacebookUser save(FacebookUser user) {
        try {
            entityManager.persist(user);
            return user;
        } catch (Exception e) {
            log.error("Failed to persist FacebookUser: {}", user, e);
            throw new RuntimeException("Failed to save FacebookUser", e);
        }
    }

    @Override
    public FacebookUser get(String id) {
        try {
            return entityManager.find(FacebookUser.class, id);
        } catch (Exception e) {
            log.error("Error getting FacebookUser with id={}", id, e);
            throw new RuntimeException("Failed to get FacebookUser", e);
        }
    }

    @Override
    public List<FacebookUser> search(String fullName) {
        try {
            return entityManager.createQuery(
                            "SELECT f FROM FacebookUser f WHERE LOWER(f.fullName) LIKE LOWER(CONCAT('%', :name, '%'))",
                            FacebookUser.class)
                    .setParameter("name", fullName)
                    .getResultList();
        } catch (Exception e) {
            log.error("Error searching FacebookUser with name={}", fullName, e);
            throw new RuntimeException("Failed to search FacebookUsers", e);
        }
    }
    @Override
    public Map<String, Object> search(FacebookUserSearchCriteria criteria, Pageable pageable) {
        try {
            //TODO: create query that use LIMIT ?, ?
            //TODO: set ? ? with pageable
            //TODO: for every field in criteria, set typed parameter to WHERE condition
            StringBuilder queryBuilder = new StringBuilder("SELECT f FROM FacebookUser f WHERE 1=1");
            StringBuilder countQueryBuilder = new StringBuilder("SELECT COUNT(f) FROM FacebookUser f WHERE 1=1");

            if (criteria.getFacebookId() != null && !criteria.getFacebookId().isBlank()) {
                countQueryBuilder.append(" AND f.facebookId LIKE :facebookId");
                queryBuilder.append(" AND f.facebookId LIKE :facebookId");
            }
            if (criteria.getFacebookName() != null && !criteria.getFacebookName().isBlank()) {
                countQueryBuilder.append(" AND LOWER(f.facebookName) LIKE LOWER(:facebookName)");
                queryBuilder.append(" AND LOWER(f.facebookName) LIKE LOWER(:facebookName)");
            }
            if (criteria.getRealName() != null && !criteria.getRealName().isBlank()) {
                countQueryBuilder.append(" AND LOWER(f.realName) LIKE LOWER(:realName)");
                queryBuilder.append(" AND LOWER(f.realName) LIKE LOWER(:realName)");
            }
            if (criteria.getEmail() != null && !criteria.getEmail().isBlank()) {
                countQueryBuilder.append(" AND LOWER(f.email) LIKE LOWER(:email)");
                queryBuilder.append(" AND LOWER(f.email) LIKE LOWER(:email)");
            }
            if (criteria.getPhone() != null && !criteria.getPhone().isBlank()) {
                countQueryBuilder.append(" AND f.phone LIKE :phone");
                queryBuilder.append(" AND f.phone LIKE :phone");
            }
            if (criteria.getZalo() != null && !criteria.getZalo().isBlank()) {
                countQueryBuilder.append(" AND f.zalo LIKE :zalo");
                queryBuilder.append(" AND f.zalo LIKE :zalo");
            }

            var query = entityManager.createQuery(queryBuilder.toString(), FacebookUser.class);
            var countQuery = entityManager.createQuery(countQueryBuilder.toString(), Long.class);
            if (criteria.getFacebookId() != null && !criteria.getFacebookId().isBlank()) {
                countQuery.setParameter("facebookId", "%" + criteria.getFacebookId() + "%");
                query.setParameter("facebookId", "%" + criteria.getFacebookId() + "%");
            }
            if (criteria.getFacebookName() != null && !criteria.getFacebookName().isBlank()) {
                countQuery.setParameter("facebookName", "%" + criteria.getFacebookName() + "%");
                query.setParameter("facebookName", "%" + criteria.getFacebookName() + "%");
            }
            if (criteria.getRealName() != null && !criteria.getRealName().isBlank()) {
                countQuery.setParameter("realName", "%" + criteria.getRealName() + "%");
                query.setParameter("realName", "%" + criteria.getRealName() + "%");
            }
            if (criteria.getEmail() != null && !criteria.getEmail().isBlank()) {
                countQuery.setParameter("email", "%" + criteria.getEmail() + "%");
                query.setParameter("email", "%" + criteria.getEmail() + "%");
            }
            if (criteria.getPhone() != null && !criteria.getPhone().isBlank()) {
                countQuery.setParameter("phone", "%" + criteria.getPhone() + "%");
                query.setParameter("phone", "%" + criteria.getPhone() + "%");
            }
            if (criteria.getZalo() != null && !criteria.getZalo().isBlank()) {
                countQuery.setParameter("zalo", "%" + criteria.getZalo() + "%");
                query.setParameter("zalo", "%" + criteria.getZalo() + "%");
            }

            if (pageable.isPaged()) {
                query.setFirstResult((int) pageable.getOffset()); // offset từ Pageable
                query.setMaxResults(pageable.getPageSize());      // limit = page size
            }
            List<FacebookUser> resultList = query.getResultList(); // ✅ sửa lại, bạn đang gọi .getSingleResult() sai
            Long totalElements = countQuery.getSingleResult();     // ✅ đếm số lượng

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("content", resultList);
            resultMap.put("totalElements", totalElements);
            return resultMap;
        } catch (Exception e) {
            log.error("Error searching FacebookUser", e);
            throw new RuntimeException("Failed to search FacebookUsers", e);
        }
    }

    @Override
    public List<FacebookUser> getAll() {
        try {
            return entityManager.createQuery("SELECT f FROM FacebookUser f", FacebookUser.class)
                    .getResultList();
        } catch (Exception e) {
            log.error("Error retrieving all FacebookUsers", e);
            throw new RuntimeException("Failed to get all FacebookUsers", e);
        }
    }

    @Override
    public FacebookUser update(FacebookUser updatedUser) {
        try {
            FacebookUser existing = entityManager.find(FacebookUser.class, updatedUser.getFacebookId());
            if (existing == null) return null;
            if (updatedUser.getFacebookName() != null && !Objects.equals(existing.getFacebookName(), updatedUser.getFacebookName())) {
                existing.setFacebookName(updatedUser.getFacebookName());
            }
            if (updatedUser.getRealName() != null && !Objects.equals(existing.getRealName(), updatedUser.getRealName())) {
                existing.setRealName(updatedUser.getRealName());
            }
            if (updatedUser.getEmail() != null && !Objects.equals(existing.getEmail(), updatedUser.getEmail())) {
                existing.setEmail(updatedUser.getEmail());
            }
            if (updatedUser.getPhone() != null && !Objects.equals(existing.getPhone(), updatedUser.getPhone())) {
                existing.setPhone(updatedUser.getPhone());
            }
            if (updatedUser.getZalo() != null && !Objects.equals(existing.getZalo(), updatedUser.getZalo())) {
                existing.setZalo(updatedUser.getZalo());
            }

            return existing;
        } catch (Exception e) {
            log.error("Error updating FacebookUser: {}", updatedUser, e);
            throw new RuntimeException("Failed to update FacebookUser", e);
        }
    }

    @Override
    public boolean existsById(String id) {
        try {
            return entityManager.find(FacebookUser.class, id) != null;
        } catch (Exception e) {
            log.error("Error checking existence of FacebookUser with id={}", id, e);
            throw new RuntimeException("Failed to check existence", e);
        }
    }

    @Override
    public void deleteById(String facebookUserId) {
        try {
            FacebookUser entity = entityManager.find(FacebookUser.class, facebookUserId);
            if (entity != null) {
                entityManager.remove(entity);
            } else {
                log.warn("FacebookUser not found with id: {}", facebookUserId);
            }
        } catch (Exception e) {
            log.error("Delete FacebookUser failed {}", e.getMessage(), e);
            throw new RuntimeException("Delete FacebookUser failed", e);
        }
    }
}
