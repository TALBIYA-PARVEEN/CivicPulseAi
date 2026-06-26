package com.talbiya.CivicPulseAi.controller;

import com.talbiya.CivicPulseAi.dto.NotificationResponse;
import com.talbiya.CivicPulseAi.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public List<NotificationResponse> getMyNotifications() {

        return notificationService.getMyNotifications();
    }
}
