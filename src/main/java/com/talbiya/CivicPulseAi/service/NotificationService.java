package com.talbiya.CivicPulseAi.service;

import com.talbiya.CivicPulseAi.dto.NotificationResponse;
import com.talbiya.CivicPulseAi.entity.User;

import java.util.List;

public interface NotificationService {

    void createNotification(
            User user,
            String message);

    List<NotificationResponse> getMyNotifications();


}