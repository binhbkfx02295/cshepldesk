package com.binhbkfx02295.cshelpdesk.facebookgraphapi.service;

import com.binhbkfx02295.cshelpdesk.facebookgraphapi.dto.FacebookUserProfileDTO;
import com.binhbkfx02295.cshelpdesk.facebookgraphapi.entity.FacebookToken;
import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserFetchDTO;

public interface FacebookGraphAPIService {

    /**
     * Save a short-lived access token by exchanging it with a long-lived token and persisting it.
     * @param shortLivedToken the short-lived token from Facebook
     */
    FacebookToken saveShortLivedToken(String shortLivedToken);

    /**
     * Get a valid (not-expired) access token. Automatically renews if necessary.
     * @return valid access token string
     */
    String getValidAccessToken();

    /**
     * Get Facebook user profile by user ID using current access token.
     * @param userId the Facebook user ID
     * @return user profile data DTO
     */
    FacebookUserProfileDTO getUserProfile(String userId);

    void notifyNoAssignee(String senderId);
}
