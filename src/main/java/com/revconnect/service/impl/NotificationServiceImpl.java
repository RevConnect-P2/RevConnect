package com.revconnect.service.impl;

import com.revconnect.dto.response.NotificationResponse;
import com.revconnect.entity.Notification;
import com.revconnect.entity.User;
import com.revconnect.enums.NotificationType;
import com.revconnect.mapper.NotificationMapper;
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

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final UserRepository userRepository;

    @Override
    public void createNotification(Long senderId,
                                   Long receiverId,
                                   Long referenceId,
                                   NotificationType type,
                                   String extraText) {

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String username = sender.getUsername();

        String message = "";

        switch (type) {

            case LIKE:
                message = username + " liked your post";
                break;

            case COMMENT:
                message = username + " commented: \"" + extraText + "\"";
                break;

            case FOLLOW:
                message = username + " started following you";
                break;

            case CONNECTION:
                message = username + " sent you a connection request";
                break;

            case SHARE:
                message = username + " shared your post";
                break;

            case POST:
                message = username + " posted a new update";
                break;
        }

        Notification notification = new Notification();

        notification.setSenderId(senderId);
        notification.setReceiverId(receiverId);
        notification.setReferenceId(referenceId);
        notification.setType(type);
        notification.setMessage(message);

        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationResponse> getUserNotifications(Long userId) {

        return notificationRepository
                .findByReceiverIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(notificationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public long getUnreadCount(Long userId) {

        return notificationRepository
                .countByReceiverIdAndIsReadFalse(userId);
    }

    @Override
    public void markAsRead(Long id) {

        Notification notification = notificationRepository
                .findById(id)
                .orElseThrow();

        notification.setRead(true);

        notificationRepository.save(notification);
    }

    @Override
    public void deleteNotification(Long id) {

        notificationRepository.deleteById(id);
    }

    @Override
    public List<NotificationResponse> getNotificationsByType(Long userId,
                                                             NotificationType type) {

        return notificationRepository
                .findByReceiverIdAndType(userId, type)
                .stream()
                .map(notificationMapper::toResponse)
                .collect(Collectors.toList());
    }
    @Override
    public void markAsUnread(Long id){

        Notification notification =
                notificationRepository.findById(id).orElseThrow();

        notification.setRead(false);

        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(Long userId){

        List<Notification> notifications =
                notificationRepository.findByReceiverIdAndIsReadFalse(userId);

        notifications.forEach(n -> n.setRead(true));

        notificationRepository.saveAll(notifications);
    }
    @Override
    public long getUnreadCountByType(Long userId, NotificationType type) {

        return notificationRepository
                .countByReceiverIdAndTypeAndIsReadFalse(userId, type);
    }
}