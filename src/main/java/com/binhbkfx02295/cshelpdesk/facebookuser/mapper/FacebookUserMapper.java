package com.binhbkfx02295.cshelpdesk.facebookuser.mapper;

import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserExportDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserListDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserDetailDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserFetchDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.entity.FacebookUser;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
public class FacebookUserMapper {
    public FacebookUserListDTO toListDTO(FacebookUser entity) {
        FacebookUserListDTO dto = new FacebookUserListDTO();
        dto.setFacebookId(entity.getFacebookId());
        dto.setFacebookName(entity.getFacebookName());
        dto.setFacebookProfilePic(entity.getFacebookProfilePic());
        return dto;
    };
    public FacebookUserDetailDTO toDetailDTO(FacebookUser entity) {
        FacebookUserDetailDTO dto = new FacebookUserDetailDTO();
        dto.setFacebookId(entity.getFacebookId());
        dto.setFacebookName(entity.getFacebookName());
        dto.setFacebookProfilePic(entity.getFacebookProfilePic());
        dto.setRealName(entity.getRealName());
        dto.setEmail(entity.getEmail());
        dto.setZalo(entity.getZalo());
        dto.setPhone(entity.getPhone());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    };
    public FacebookUser toEntity(FacebookUserDetailDTO dto) {
        FacebookUser entity = new FacebookUser();
        entity.setFacebookId(dto.getFacebookId());
        entity.setFacebookName(dto.getFacebookName());
        entity.setFacebookProfilePic(dto.getFacebookProfilePic());
        entity.setRealName(dto.getRealName());
        entity.setEmail(dto.getEmail());
        entity.setZalo(dto.getZalo());
        entity.setPhone(dto.getPhone());
        entity.setCreatedAt(dto.getCreatedAt());
        return entity;
    }

    public FacebookUser toEntity(FacebookUserListDTO dto) {
        FacebookUser entity = new FacebookUser();
        entity.setFacebookId(dto.getFacebookId());
        entity.setFacebookName(dto.getFacebookName());
        entity.setFacebookProfilePic(dto.getFacebookProfilePic());
        return entity;
    }

    public FacebookUser toEntity(FacebookUserFetchDTO dto) {
        FacebookUser entity = new FacebookUser();
        entity.setFacebookId(dto.getFacebookId());
        entity.setFacebookName(String.format("%s %s", dto.getFacebookFirstName(), dto.getFacebookLastName()));
        entity.setFacebookProfilePic(dto.getFacebookProfilePic());
        return entity;
    }

    public FacebookUserListDTO toDTO(FacebookUserDetailDTO facebookUser) {
        FacebookUserListDTO dto = new FacebookUserListDTO();
        dto.setFacebookId(facebookUser.getFacebookId());
        dto.setFacebookName(facebookUser.getFacebookName());
        dto.setFacebookProfilePic(facebookUser.getFacebookProfilePic());
        return dto;
    }

    public FacebookUserExportDTO toExportDTO(FacebookUser entity) {
        FacebookUserExportDTO dto = new FacebookUserExportDTO();
        dto.setFacebookId(entity.getFacebookId());
        dto.setFacebookName(entity.getFacebookName());
        dto.setRealName(entity.getRealName());
        dto.setEmail(entity.getEmail());
        dto.setZalo(entity.getZalo());
        dto.setPhone(entity.getPhone());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
