package com.revconnect.repository;

import com.revconnect.entity.Notification;
import com.revconnect.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // =========================================
    // GET ALL NOTIFICATIONS FOR USER
    // =========================================
    List<Notification> findByReceiver_UserIdOrderByCreatedAtDesc(Long receiverId);


    // =========================================
    // COUNT UNREAD NOTIFICATIONS
    // =========================================
    long countByReceiver_UserIdAndReadFalse(Long receiverId);


    // =========================================
    // GET NOTIFICATIONS BY TYPE
    // =========================================
    List<Notification> findByReceiver_UserIdAndTypeOrderByCreatedAtDesc(
            Long receiverId,
            NotificationType type
    );


    // =========================================
    // COUNT UNREAD NOTIFICATIONS BY TYPE
    // =========================================
    long countByReceiver_UserIdAndTypeAndReadFalse(
            Long receiverId,
            NotificationType type
    );


    // =========================================
    // GET ALL UNREAD NOTIFICATIONS
    // =========================================
    List<Notification> findByReceiver_UserIdAndReadFalseOrderByCreatedAtDesc(
            Long receiverId
    );


    // =========================================
    // DELETE ALL USER NOTIFICATIONS
    // =========================================
    void deleteByReceiver_UserId(Long receiverId);

}