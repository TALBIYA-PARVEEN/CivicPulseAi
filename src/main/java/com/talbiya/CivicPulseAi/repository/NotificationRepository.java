package com.talbiya.CivicPulseAi.repository;

import com.talbiya.CivicPulseAi.entity.Notification;
import com.talbiya.CivicPulseAi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByUser(User user);
}
