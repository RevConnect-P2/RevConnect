package com.revconnect.service.impl;

import com.revconnect.dto.response.NotificationResponse;
import com.revconnect.entity.Notification;
import com.revconnect.entity.NotificationPreference;
import com.revconnect.entity.User;
import com.revconnect.enums.NotificationType;
import com.revconnect.mapper.NotificationMapper;
import com.revconnect.repository.NotificationPreferenceRepository;
import com.revconnect.repository.NotificationRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.NotificationService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationPreferenceRepository preferenceRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final UserRepository userRepository;


    // CREATE NOTIFICATION

    @Override
    public void createNotification(Long senderId,
                                   Long receiverId,
                                   Long referenceId,
                                   NotificationType type,
                                   String extraText) {

        // Prevent self-notifications
        if (senderId.equals(receiverId)) {
            return;
        }

        if (!isNotificationEnabled(receiverId)) {
            return;
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        String message = buildMessage(sender.getUsername(), type, extraText);

        Notification notification = new Notification();
        notification.setSender(sender);
        notification.setReceiver(receiver);
        notification.setReferenceId(referenceId);
        notification.setType(type);
        notification.setMessage(message);
        notification.setRead(false);

        notificationRepository.save(notification);

        System.out.println("Notification created: " + message);
    }


    // MESSAGE BUILDER

    private String buildMessage(String username,
                                NotificationType type,
                                String extraText) {

        return switch (type) {
            case LIKE -> username + " liked your post";
            case COMMENT -> username + " commented: \"" + (extraText != null ? extraText : "") + "\"";
            case FOLLOW -> username + " started following you";
            case CONNECTION -> username + " sent you a connection request";
            case SHARE -> username + " shared your post";
            case POST -> username + " created a new post";
            case CONNECTION_ACCEPTED -> username + " accepted your connection request";
            case CONNECTION_REJECTED -> username + " rejected your connection request";
            default -> username + " sent a notification";
        };
    }


    // GET USER NOTIFICATIONS

    @Override
    public List<NotificationResponse> getUserNotifications(Long userId) {

        return notificationRepository
                .findByReceiver_UserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(notificationMapper::toResponse)
                .collect(Collectors.toList());
    }


    // GET UNREAD COUNT

    @Override
    public long getUnreadCount(Long userId) {

        return notificationRepository
                .countByReceiver_UserIdAndReadFalse(userId);
    }


    // MARK AS READ

    @Override
    public void markAsRead(Long notificationId) {

        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);

        notificationRepository.save(notification);
    }

    // MARK AS UNREAD

    @Override
    public void markAsUnread(Long notificationId) {

        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(false);

        notificationRepository.save(notification);
    }


    // MARK ALL AS READ

    @Override
    public void markAllAsRead(Long userId) {

        List<Notification> notifications =
                notificationRepository
                        .findByReceiver_UserIdAndReadFalseOrderByCreatedAtDesc(userId);

        notifications.forEach(n -> n.setRead(true));

        notificationRepository.saveAll(notifications);
    }


    // DELETE NOTIFICATION

    @Override
    public void deleteNotification(Long notificationId) {

        notificationRepository.deleteById(notificationId);
    }


    // FILTER BY TYPE

    @Override
    public List<NotificationResponse> getNotificationsByType(Long userId,
                                                             NotificationType type) {

        return notificationRepository
                .findByReceiver_UserIdAndTypeOrderByCreatedAtDesc(userId, type)
                .stream()
                .map(notificationMapper::toResponse)
                .collect(Collectors.toList());
    }


    // UNREAD COUNT BY TYPE

    @Override
    public long getUnreadCountByType(Long userId,
                                     NotificationType type) {

        return notificationRepository
                .countByReceiver_UserIdAndTypeAndReadFalse(userId, type);
    }

    @Override
    public void enableNotifications(Long userId) {

        NotificationPreference pref =
                preferenceRepository.findByUser_UserId(userId)
                        .orElse(null);

        if (pref == null) {

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            pref = NotificationPreference.builder()
                    .user(user)
                    .enabled(true)
                    .build();

        } else {
            pref.setEnabled(true);
        }

        preferenceRepository.save(pref);
    }

    @Override
    public void disableNotifications(Long userId) {

        NotificationPreference pref =
                preferenceRepository.findByUser_UserId(userId)
                        .orElse(null);

        if (pref == null) {

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            pref = NotificationPreference.builder()
                    .user(user)
                    .enabled(false)
                    .build();

        } else {
            pref.setEnabled(false);
        }

        preferenceRepository.save(pref);
    }

    @Override
    public boolean isNotificationEnabled(Long userId) {

        return preferenceRepository.findByUser_UserId(userId)
                .map(NotificationPreference::getEnabled)
                .orElse(true); // default enabled
    }
}