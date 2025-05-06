package com.binhbkfx02295.cshelpdesk.webhook.service;

import com.binhbkfx02295.cshelpdesk.webhook.dto.WebHookEventDTO;

public interface WebHookService {
    void handleWebhook(WebHookEventDTO event);
}