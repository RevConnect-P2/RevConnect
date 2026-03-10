package com.revconnect.controller;

import com.revconnect.dto.response.NotificationResponse;
import com.revconnect.entity.User;
import com.revconnect.enums.NotificationType;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.NotificationService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

// ✅ LOGGER IMPORTS
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    // ✅ LOGGER OBJECT
    private static final Logger logger =
            LogManager.getLogger(NotificationController.class);

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    // =========================
    // HELPER METHOD
    // =========================
    private User getLoggedInUser(Principal principal) {

        logger.info("Fetching logged-in user");

        if (principal == null) {

            logger.error("Principal is null - user not authenticated");

            throw new RuntimeException("User not authenticated");
        }

        String email = principal.getName();

        logger.info("Logged-in user email: {}", email);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> {

                    logger.error("User not found for email: {}", email);

                    return new RuntimeException("User not found");
                });
    }

    // =========================
    // NOTIFICATION COUNT (NAVBAR)
    // =========================
    @ModelAttribute
    public void notificationCount(Model model, Principal principal) {

        logger.info("Calculating unread notification count");

        if (principal == null) {
            model.addAttribute("unreadCount", 0);
            return;
        }

        User user = getLoggedInUser(principal);

        long unreadCount =
                notificationService.getUnreadCount(user.getUserId());

        logger.info("Unread notifications for user {} = {}", user.getUserId(), unreadCount);

        model.addAttribute("unreadCount", unreadCount);
    }

    // =========================
    // VIEW ALL NOTIFICATIONS
    // =========================
    @GetMapping
    public String viewNotifications(Model model, Principal principal) {

        logger.info("Viewing all notifications");

        User user = getLoggedInUser(principal);

        List<NotificationResponse> notifications =
                notificationService.getUserNotifications(user.getUserId());

        logger.info("Fetched {} notifications for user {}", notifications.size(), user.getUserId());

        model.addAttribute("notifications", notifications);

        return "notifications/notifications";
    }

    // =========================
    // FILTER NOTIFICATIONS
    // =========================
    @GetMapping("/filter")
    public String filterNotifications(@RequestParam(required = false) NotificationType type,
                                      Model model,
                                      Principal principal) {

        logger.info("Filtering notifications by type: {}", type);

        User user = getLoggedInUser(principal);

        List<NotificationResponse> notifications;
        long unreadCount;

        if (type == null) {

            logger.info("Fetching all notifications");

            notifications =
                    notificationService.getUserNotifications(user.getUserId());

            unreadCount =
                    notificationService.getUnreadCount(user.getUserId());

        } else {

            logger.info("Fetching notifications of type {} for user {}", type, user.getUserId());

            notifications =
                    notificationService.getNotificationsByType(user.getUserId(), type);

            unreadCount =
                    notificationService.getUnreadCountByType(user.getUserId(), type);
        }

        model.addAttribute("notifications", notifications);
        model.addAttribute("selectedType", type);
        model.addAttribute("unreadCount", unreadCount);

        return "notifications/notifications";
    }

    // =========================
    // MARK SINGLE NOTIFICATION AS READ
    // =========================
    @PostMapping("/read/{id}")
    public String markRead(@PathVariable Long id,
                           @RequestParam(required = false) NotificationType type) {

        logger.info("Marking notification {} as read", id);

        notificationService.markAsRead(id);

        if (type == null) {
            return "redirect:/notifications";
        }

        return "redirect:/notifications/filter?type=" + type;
    }

    // =========================
    // MARK SINGLE NOTIFICATION AS UNREAD
    // =========================
    @PostMapping("/unread/{id}")
    public String markUnread(@PathVariable Long id,
                             @RequestParam(required = false) NotificationType type) {

        logger.info("Marking notification {} as unread", id);

        notificationService.markAsUnread(id);

        if (type == null) {
            return "redirect:/notifications";
        }

        return "redirect:/notifications/filter?type=" + type;
    }

    // =========================
    // DELETE NOTIFICATION
    // =========================
    @PostMapping("/delete/{id}")
    public String deleteNotification(@PathVariable Long id) {

        logger.info("Deleting notification {}", id);

        notificationService.deleteNotification(id);

        logger.info("Notification {} deleted successfully", id);

        return "redirect:/notifications";
    }

    // =========================
    // MARK ALL AS READ
    // =========================
    @PostMapping("/read-all")
    public String markAllRead(Principal principal) {

        logger.info("Marking all notifications as read");

        User user = getLoggedInUser(principal);

        notificationService.markAllAsRead(user.getUserId());

        logger.info("All notifications marked as read for user {}", user.getUserId());

        return "redirect:/notifications";
    }
}