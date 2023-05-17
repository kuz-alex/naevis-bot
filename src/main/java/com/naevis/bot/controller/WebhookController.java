package com.naevis.bot.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@RequestMapping("/webhook")
public class WebhookController {
    @PostMapping("/naevis")
    public String webhookNaevis(@RequestBody Update update) {
        System.out.println("test333: " + update);
        return "Naevis webhook";
    }
}
