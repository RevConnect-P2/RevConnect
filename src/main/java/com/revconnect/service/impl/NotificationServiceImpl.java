package com.revconnect.service.impl;

import com.revconnect.dto.response.NotificationResponse;
import com.revconnect.entity.Notification;
import com.revconnect.entity.User;
import com.revconnect.enums.NotificationType;
import com.revconnect.exception.ResourceNotFoundException;
import com.revconnect.repository.FollowRepository;
import com.revconnect.repository.NotificationRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;   // ✅ FIXED
    private final FollowRepository followRepository;
    @Override
    public Page<Notification> getAll(Long userId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return notificationRepository
                .findByUser_UserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Override
    public Page<Notification> getByType(Long userId,
                                        NotificationType type,
                                        int page,
                                        int size) {

        Pageable pageable = PageRequest.of(page, size);

        return notificationRepository
                .findByUser_UserIdAndTypeOrderByCreatedAtDesc(userId, type, pageable);
    }

    @Override
    @Transactional
    public String markAsRead(Long id, Long userId) {

        Notification notification = notificationRepository
                .findByNotificationIdAndUser_UserId(id, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Notification not found"));

        if (Boolean.TRUE.equals(notification.getIsRead())) {
            return "Already Read";
        }

        notification.setIsRead(true);
        return "Marked as Read";
    }

    @Override
    @Transactional
    public String markAsUnread(Long id, Long userId) {

        Notification notification = notificationRepository
                .findByNotificationIdAndUser_UserId(id, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Notification not found"));

        if (Boolean.FALSE.equals(notification.getIsRead())) {
            return "Already Unread";
        }

        notification.setIsRead(false);
        return "Marked as Unread";
    }

    @Override
    @Transactional
    public String markAllAsRead(Long userId) {

        long count = notificationRepository
                .countByUser_UserIdAndIsReadFalse(userId);

        if (count == 0) {
            return "No unread notifications";
        }

        notificationRepository.markAllAsRead(userId);

        return count + " notifications marked as read";
    }

    @Override
    public long getUnreadCount(Long userId) {
        return notificationRepository
                .countByUser_UserIdAndIsReadFalse(userId);
    }

    @Override
    @Transactional
    public void delete(Long id, Long userId) {

        Notification notification = notificationRepository
                .findByNotificationIdAndUser_UserId(id, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Notification not found"));

        notificationRepository.delete(notification);
    }

    @Override
    @Transactional
    public void createNotification(Long receiverId,
                                   Long senderId,
                                   Long referenceId,
                                   NotificationType type,
                                   String message) {

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Receiver not found"));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Sender not found"));

        Notification notification = Notification.builder()
                .user(receiver)
                .sender(sender)
                .referenceId(referenceId)
                .type(type)
                .message(message)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void notifyFollowersOfNewPost(Long authorId, Long postId) {

        // Get author
        User author = userRepository.findById(authorId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Author not found"));

        // Get followers
        List<User> followers =
                followRepository.findFollowersByFollowingId(authorId);

        for (User follower : followers) {

            Notification notification = Notification.builder()
                    .user(follower)   // receiver
                    .sender(author)   // sender
                    .referenceId(postId)
                    .type(NotificationType.NEW_POST)
                    .message(author.getUsername() + " posted something new")
                    .isRead(false)
                    .build();

            notificationRepository.save(notification);
        }
    }
    private NotificationResponse convertToDto(Notification notification) {

        return NotificationResponse.builder()
                .notificationId(notification.getNotificationId())
                .userId(notification.getUser().getUserId())   // now valid
                .senderId(notification.getSender().getUserId())
                .type(notification.getType())
                .message(notification.getMessage())
                .referenceId(notification.getReferenceId())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
    @Override
    public long getUnreadCountByType(Long userId, NotificationType type) {

        if (type == null) {
            return notificationRepository.countByUser_UserIdAndIsReadFalse(userId);
        }

        return notificationRepository
                .countByUser_UserIdAndTypeAndIsReadFalse(userId, type);
    }
}