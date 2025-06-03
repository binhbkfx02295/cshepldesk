package com.binhbkfx02295.cshelpdesk.facebookgraphapi.service;

import com.binhbkfx02295.cshelpdesk.facebookgraphapi.config.FacebookAPIProperties;
import com.binhbkfx02295.cshelpdesk.facebookgraphapi.dto.FacebookTokenResponseDTO;
import com.binhbkfx02295.cshelpdesk.facebookgraphapi.dto.FacebookUserProfileDTO;
import com.binhbkfx02295.cshelpdesk.facebookgraphapi.entity.FacebookToken;
import com.binhbkfx02295.cshelpdesk.facebookgraphapi.repository.FacebookTokenRepository;
import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserFetchDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FacebookGraphAPIServiceImpl implements FacebookGraphAPIService {

    private final FacebookTokenRepository tokenRepository;
    private final FacebookAPIProperties facebookApiProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public FacebookToken saveShortLivedToken(String shortLivedToken) {
        log.info("👉 Đang gửi yêu cầu đổi token ngắn hạn sang token dài hạn...");

        String url = String.format("https://graph.facebook.com/oauth/access_token"
                        + "?grant_type=fb_exchange_token"
                        + "&client_id=%s"
                        + "&client_secret=%s"
                        + "&fb_exchange_token=%s",
                facebookApiProperties.getAppId(),
                facebookApiProperties.getAppSecret(),
                shortLivedToken);

        FacebookTokenResponseDTO response = restTemplate.getForObject(url, FacebookTokenResponseDTO.class);

        if (response == null || response.getAccessToken() == null) {
            log.error("❌ Không thể lấy access token từ Facebook!");
            throw new IllegalStateException("Không thể lấy access token từ Facebook");
        }

        FacebookToken token = new FacebookToken();
        token.setPageId(facebookApiProperties.getPageId());
        token.setLongLivedAccessToken(response.getAccessToken());
        token.setLastUpdated(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusSeconds(response.getExpiresIn()));

        log.info("✅ Token dài hạn đã được tạo và lưu thành công.");
        return tokenRepository.save(token);
    }

    @Override
    public String getValidAccessToken() {
        log.info("🔍 Đang kiểm tra token hợp lệ cho Page ID: {}", facebookApiProperties.getPageId());
        FacebookToken token;
        Optional<FacebookToken> optionalToken =
                tokenRepository.findFirstByPageIdOrderByLastUpdatedDesc(facebookApiProperties.getPageId());

        if (optionalToken.isPresent()) {
            token = optionalToken.get();
        } else {
            token = new FacebookToken();
            token.setPageId(facebookApiProperties.getPageId());
            token.setLongLivedAccessToken(facebookApiProperties.getDefaultShortToken()); // dùng token cấu hình
            token.setExpiresAt(LocalDateTime.now().plusDays(60)); // mặc định 60 ngày kể từ giờ
            token.setLastUpdated(LocalDateTime.now());
        }

        // Kiểm tra hạn token
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = token.getExpiresAt();

        if (expiresAt.isBefore(now) || expiresAt.isEqual(now)) {
            log.warn("⚠️ Token đã hết hạn. Đang làm mới token...");
            return saveShortLivedToken(token.getLongLivedAccessToken()).getLongLivedAccessToken();
        } else if (Duration.between(now, expiresAt).toDays() <= 3) {
            log.warn("⚠️ Token sắp hết hạn trong {} ngày. Đang làm mới token...", Duration.between(now, expiresAt).toDays());
            return saveShortLivedToken(token.getLongLivedAccessToken()).getLongLivedAccessToken();
        } else {
            log.info("✅ Token hiện tại vẫn còn hiệu lực. Còn {} ngày đến hạn.", Duration.between(now, expiresAt).toDays());
            return token.getLongLivedAccessToken();
        }
    }

    @Override
    public FacebookUserProfileDTO getUserProfile(String userId) {
        log.info("📥 Đang lấy thông tin người dùng Facebook với ID: {}", userId);

        String token = getValidAccessToken();
        String url = buildProfileUrl(userId, token);

        try {
            return restTemplate.getForObject(url, FacebookUserProfileDTO.class);
        } catch (Exception e) {
            log.warn("⚠️ Gặp lỗi khi gọi API lấy profile: {}", e.getMessage());
            if (e.getMessage().contains("code\":190")) {
                log.info("🔄 Token có thể đã hết hạn. Đang làm mới token và thử lại...");
                String newToken = saveShortLivedToken(token).getLongLivedAccessToken();
                String retryUrl = buildProfileUrl(userId, newToken);
                return restTemplate.getForObject(retryUrl, FacebookUserProfileDTO.class);
            }
            log.error("❌ Lỗi không xử lý được khi lấy profile từ Facebook", e);
            return null;
        }
    }

    @Override
    public void notifyNoAssignee(String senderId) {
        log.info("🔔 Đang gửi thông báo cho khách hàng (senderId={}): không có nhân viên hỗ trợ.", senderId);

        String token = getValidAccessToken();
        String url = String.format("https://graph.facebook.com/v19.0/me/messages?access_token=%s", token);

        // 1. Tạo HashMap cho recipient và message
        Map<String, Object> payload = new HashMap<>();
        Map<String, String> recipient = new HashMap<>();
        recipient.put("id", senderId);
        Map<String, String> message = new HashMap<>();
        message.put("text", "Hi, hiện chưa có nhân viên hỗ trợ ngay lúc này. Chúng tôi sẽ liên hệ bạn sớm nhất có thể. Xin cảm ơn!");
        payload.put("recipient", recipient);
        payload.put("message", message);

        try {
            // 2. Chuyển HashMap thành JSON string
            String jsonPayload = objectMapper.writeValueAsString(payload);

            // 3. Gửi POST request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(jsonPayload, headers);

            String response = restTemplate.postForObject(url, request, String.class);
            log.info("✅ Đã gửi thông báo cho khách hàng thành công. Response: {}", response);
        } catch (Exception e) {
            log.error("❌ Gửi thông báo cho khách hàng thất bại", e);
        }
    }

    private String buildProfileUrl(String userId, String token) {
        return String.format("https://graph.facebook.com/%s?fields=id,first_name,last_name,picture,email&access_token=%s",
                userId, token);
    }
}
