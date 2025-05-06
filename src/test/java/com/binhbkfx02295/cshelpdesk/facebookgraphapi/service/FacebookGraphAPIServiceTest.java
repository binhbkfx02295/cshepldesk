package com.binhbkfx02295.cshelpdesk.facebookgraphapi.service;

import com.binhbkfx02295.cshelpdesk.facebookgraphapi.config.FacebookAPIProperties;
import com.binhbkfx02295.cshelpdesk.facebookgraphapi.dto.FacebookTokenResponseDTO;
import com.binhbkfx02295.cshelpdesk.facebookgraphapi.dto.FacebookUserProfileDTO;
import com.binhbkfx02295.cshelpdesk.facebookgraphapi.entity.FacebookToken;
import com.binhbkfx02295.cshelpdesk.facebookgraphapi.repository.FacebookTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FacebookGraphAPIServiceTest {

    @InjectMocks
    private FacebookGraphAPIServiceImpl facebookGraphAPIService;

    @Mock
    private FacebookTokenRepository tokenRepository;

    @Mock
    private FacebookAPIProperties facebookApiProperties;

    @Mock
    private RestTemplate restTemplate;

    @Captor
    ArgumentCaptor<FacebookToken> tokenCaptor;

    private final String fakeShortToken = "short_token";
    private final String longLivedToken = "long_lived_token";
    private final String pageId = "123456789";
    private final String userId = "999999";

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        when(facebookApiProperties.getAppId()).thenReturn("app_id");
        when(facebookApiProperties.getAppSecret()).thenReturn("app_secret");
        when(facebookApiProperties.getPageId()).thenReturn(pageId);
    }

    @Test
    void test_saveShortLivedToken_shouldExchangeAndSave() {
        String urlExpected = String.format(
                "https://graph.facebook.com/oauth/access_token" +
                        "?grant_type=fb_exchange_token&client_id=%s&client_secret=%s&fb_exchange_token=%s",
                "app_id", "app_secret", fakeShortToken
        );

        var mockResponse = new FacebookTokenResponseDTO();
        mockResponse.setAccessToken(longLivedToken);
        mockResponse.setTokenType("bearer");
        mockResponse.setExpiresIn(5183944); // ~60 days

        when(restTemplate.getForObject(eq(urlExpected), eq(FacebookTokenResponseDTO.class)))
                .thenReturn(mockResponse);

        facebookGraphAPIService.saveShortLivedToken(fakeShortToken);

        verify(tokenRepository).save(tokenCaptor.capture());
        FacebookToken saved = tokenCaptor.getValue();

        assertEquals(longLivedToken, saved.getLongLivedAccessToken());
        assertEquals(pageId, saved.getPageId());
        assertTrue(saved.getExpiresAt().isAfter(LocalDateTime.now()));
    }

    @Test
    void test_getValidToken_shouldReturnExistingTokenIfNotExpired() {
        FacebookToken token = new FacebookToken();
        token.setPageId(pageId);
        token.setLongLivedAccessToken(longLivedToken);
        token.setExpiresAt(LocalDateTime.now().plusDays(10));
        when(tokenRepository.findFirstByPageIdOrderByLastUpdatedDesc(pageId)).thenReturn(Optional.of(token));

        String result = facebookGraphAPIService.getValidAccessToken();
        assertEquals(longLivedToken, result);
    }

    @Test
    void test_getValidToken_shouldRenewTokenIfExpiredSoon() {
        FacebookToken oldToken = new FacebookToken();
        oldToken.setPageId(pageId);
        oldToken.setLongLivedAccessToken("old_token");
        oldToken.setExpiresAt(LocalDateTime.now().plusHours(1)); // sắp hết hạn

        FacebookTokenResponseDTO newToken = new FacebookTokenResponseDTO();
        newToken.setAccessToken(longLivedToken);
        newToken.setExpiresIn(5183944);

        when(tokenRepository.findFirstByPageIdOrderByLastUpdatedDesc(pageId))
                .thenReturn(Optional.of(oldToken));
        when(restTemplate.getForObject(contains("oauth/access_token"), eq(FacebookTokenResponseDTO.class)))
                .thenReturn(newToken);
        when(tokenRepository.save(any())).thenReturn(new FacebookToken(
                0, pageId, longLivedToken,
                LocalDateTime.now(), LocalDateTime.now().plusDays(60)
        ));
        String result = facebookGraphAPIService.getValidAccessToken();

        verify(tokenRepository).save(any());
        assertEquals(longLivedToken, result);
    }

    @Test
    void test_getUserProfile_shouldCallFacebookGraphAndReturnDTO() {
        FacebookUserProfileDTO mockUser = new FacebookUserProfileDTO();
        mockUser.setId(userId);
        mockUser.setFirstName("Binh");
        mockUser.setLastName("Nguyen");

        String fullUrl = String.format(
                "https://graph.facebook.com/%s?fields=id,first_name,last_name,picture,email&access_token=%s",
                userId, longLivedToken
        );

        FacebookToken token = new FacebookToken();
        token.setLongLivedAccessToken(longLivedToken);
        token.setExpiresAt(LocalDateTime.now().plusDays(5));
        when(tokenRepository.findFirstByPageIdOrderByLastUpdatedDesc(pageId)).thenReturn(Optional.of(token));
        when(restTemplate.getForObject(eq(fullUrl), eq(FacebookUserProfileDTO.class)))
                .thenReturn(mockUser);

        FacebookUserProfileDTO result = facebookGraphAPIService.getUserProfile(userId);
        assertEquals("Binh", result.getFirstName());
        assertEquals("Nguyen", result.getLastName());
    }
}
