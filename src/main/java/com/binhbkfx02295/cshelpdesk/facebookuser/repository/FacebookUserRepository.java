package com.binhbkfx02295.cshelpdesk.facebookuser.repository;

import com.binhbkfx02295.cshelpdesk.facebookuser.entity.FacebookUser;
import com.binhbkfx02295.cshelpdesk.facebookuser.repository.dao.FacebookUserDAO;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

            if (updatedUser.getFacebookFirstName() != null) existing.setFacebookFirstName(updatedUser.getFacebookFirstName());
            if (updatedUser.getFacebookLastName() != null) existing.setFacebookLastName(updatedUser.getFacebookLastName());
            if (updatedUser.getFacebookProfilePic() != null) existing.setFacebookProfilePic(updatedUser.getFacebookProfilePic());
            if (updatedUser.getEmail() != null) existing.setEmail(updatedUser.getEmail());
            if (updatedUser.getPhone() != null) existing.setPhone(updatedUser.getPhone());
            if (updatedUser.getZalo() != null) existing.setZalo(updatedUser.getZalo());

            return entityManager.merge(existing);
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
