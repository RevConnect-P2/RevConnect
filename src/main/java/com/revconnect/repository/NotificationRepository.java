package com.revconnect.repository;

import com.revconnect.entity.Notification;
import com.revconnect.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);

    long countByReceiverIdAndIsReadFalse(Long receiverId);

    List<Notification> findByReceiverIdAndType(Long receiverId,
                                               NotificationType type);

    long countByReceiverIdAndTypeAndIsReadFalse(Long receiverId, NotificationType type);

    List<Notification> findByReceiverIdAndIsReadFalse(Long receiverId);

}