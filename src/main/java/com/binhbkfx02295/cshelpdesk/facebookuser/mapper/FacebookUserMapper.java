package com.binhbkfx02295.cshelpdesk.facebookuser.mapper;

import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserDetailDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.entity.FacebookUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
public class FacebookUserMapper {
    public FacebookUserDTO toListDTO(FacebookUser entity) {
        FacebookUserDTO dto = new FacebookUserDTO();
        dto.setFacebookId(entity.getFacebookId());
        dto.setFacebookLastName(entity.getFacebookLastName());
        dto.setFacebookFirstName(entity.getFacebookFirstName());
        dto.setFacebookProfilePic(entity.getFacebookProfilePic());
        return dto;
    };
    public FacebookUserDetailDTO toDetailDTO(FacebookUser entity) {
        FacebookUserDetailDTO dto = new FacebookUserDetailDTO();
        dto.setFacebookId(entity.getFacebookId());
        dto.setEmail(entity.getEmail());
        dto.setZalo(entity.getZalo());
        dto.setPhone(entity.getPhone());
        dto.setFacebookLastName(entity.getFacebookLastName());
        dto.setFacebookFirstName(entity.getFacebookFirstName());
        dto.setPhone(entity.getPhone());
        dto.setEmail(entity.getEmail());
        dto.setFacebookProfilePic(entity.getFacebookProfilePic());
        return dto;
    };
    public FacebookUser toEntity(FacebookUserDetailDTO dto) {
        FacebookUser entity = new FacebookUser();
        entity.setFacebookId(dto.getFacebookId());
        entity.setEmail(dto.getEmail());
        entity.setZalo(dto.getZalo());
        entity.setPhone(dto.getPhone());
        entity.setFacebookLastName(dto.getFacebookLastName());
        entity.setFacebookFirstName(dto.getFacebookFirstName());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());
        entity.setFacebookProfilePic(dto.getFacebookProfilePic());
        return entity;
    }
    ;
}
