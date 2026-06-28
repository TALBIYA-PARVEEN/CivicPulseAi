package com.talbiya.CivicPulseAi.service;

import com.talbiya.CivicPulseAi.entity.Issue;
import com.talbiya.CivicPulseAi.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class AlertService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // 🔔 Notify user (WebSocket)
    public void sendUserAlert(User user, String message) {

        messagingTemplate.convertAndSendToUser(
                user.getEmail(),
                "/queue/alerts",
                message
        );
    }

    // 🚨 Global system alert (admin dashboard)
    public void sendGlobalAlert(String message) {

        messagingTemplate.convertAndSend(
                "/topic/alerts",
                message
        );
    }

    // 🚧 Issue-based alert
    public void sendIssueAlert(Issue issue, String message) {

        messagingTemplate.convertAndSend(
                "/topic/issues/" + issue.getId(),
                message
        );
    }
}