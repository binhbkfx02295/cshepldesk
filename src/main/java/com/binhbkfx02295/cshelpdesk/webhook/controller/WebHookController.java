package com.binhbkfx02295.cshelpdesk.webhook.controller;

import com.binhbkfx02295.cshelpdesk.facebookgraphapi.service.FacebookGraphAPIService;
import com.binhbkfx02295.cshelpdesk.webhook.dto.WebHookEventDTO;
import com.binhbkfx02295.cshelpdesk.webhook.service.WebHookService;
import com.binhbkfx02295.cshelpdesk.webhook.service.WebHookServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
@Slf4j
public class WebHookController {

    private final WebHookServiceImpl webhookService;

    // Facebook webhook verify (GET)
    @GetMapping
    public ResponseEntity<String> verify(@RequestParam("hub.mode") String mode,
                                         @RequestParam("hub.verify_token") String token,
                                         @RequestParam("hub.challenge") String challenge) {
        if ("subscribe".equals(mode) && "your_verify_token".equals(token)) {

            return ResponseEntity.ok(challenge);
        }
        return ResponseEntity.status(403).body("Verification failed");
    }

    @PostMapping
    public ResponseEntity<String> receive(
            HttpServletRequest request) throws IOException {
        String rawBody = new BufferedReader(new InputStreamReader(request.getInputStream()))
                .lines().collect(Collectors.joining("\n"));
        log.info("[Webhook] Raw body:\n{}", rawBody);

        WebHookEventDTO event = new ObjectMapper().readValue(rawBody, WebHookEventDTO.class);
        webhookService.handleWebhook(event);
        log.info("[Webhook] Raw payload received: {}", event);
        return ResponseEntity.ok("EVENT_RECEIVED");
    }
}
