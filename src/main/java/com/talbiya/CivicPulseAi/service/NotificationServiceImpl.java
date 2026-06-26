package com.talbiya.CivicPulseAi.service;

import com.talbiya.CivicPulseAi.dto.NotificationResponse;
import com.talbiya.CivicPulseAi.entity.Notification;
import com.talbiya.CivicPulseAi.entity.User;
import com.talbiya.CivicPulseAi.repository.NotificationRepository;
import com.talbiya.CivicPulseAi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl
        implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void createNotification(
            User user,
            String message) {

        Notification notification =
                new Notification();

        notification.setUser(user);
        notification.setMessage(message);
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationResponse> getMyNotifications() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return notificationRepository.findByUser(user)
                .stream()
                .map(notification ->
                        new NotificationResponse(
                                notification.getId(),
                                notification.getMessage(),
                                notification.getIsRead(),
                                notification.getCreatedAt()
                        ))
                .toList();
    }
}