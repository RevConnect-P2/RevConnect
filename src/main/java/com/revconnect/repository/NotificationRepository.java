package com.revconnect.repository;

import com.revconnect.entity.Notification;
import com.revconnect.enums.NotificationType;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUser_UserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<Notification> findByUser_UserIdAndTypeOrderByCreatedAtDesc(
            Long userId,
            NotificationType type,
            Pageable pageable);

    Optional<Notification> findByNotificationIdAndUser_UserId(
            Long id,
            Long userId);

    long countByUser_UserIdAndIsReadFalse(Long userId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.userId = :userId")
    void markAllAsRead(Long userId);

    long countByUser_UserIdAndTypeAndIsReadFalse(Long userId,NotificationType type);

}