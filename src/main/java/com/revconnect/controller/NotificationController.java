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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private static final Logger logger =
            LogManager.getLogger(NotificationController.class);

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    // =========================
    // HELPER METHOD
    // =========================
    private User getLoggedInUser(Principal principal) {

        if (principal == null) {
            throw new RuntimeException("User not authenticated");
        }

        String email = principal.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // =========================
    // NAVBAR NOTIFICATION COUNT
    // =========================
    @ModelAttribute
    public void notificationCount(Model model, Principal principal) {

        if (principal == null) {
            model.addAttribute("unreadCount", 0);
            model.addAttribute("notificationsEnabled", true);
            return;
        }

        User user = getLoggedInUser(principal);

        long unreadCount =
                notificationService.getUnreadCount(user.getUserId());

        model.addAttribute("unreadCount", unreadCount);

        Boolean enabled = user.getNotificationsEnabled();
        if (enabled == null) {
            enabled = true;
        }

        model.addAttribute("notificationsEnabled", enabled);
    }

    // =========================
    // VIEW NOTIFICATIONS
    // =========================
    @GetMapping
    public String viewNotifications(Model model, Principal principal) {

        User user = getLoggedInUser(principal);

        Boolean enabled = user.getNotificationsEnabled();

        if (enabled == null) {
            enabled = true;
        }

        model.addAttribute("notificationsEnabled", enabled);

        // If disabled → do not show notifications
        if (!enabled) {

            model.addAttribute("notifications", List.of());
            model.addAttribute("unreadCount", 0);

            return "notifications/notifications";
        }

        // If enabled → load notifications
        List<NotificationResponse> notifications =
                notificationService.getUserNotifications(user.getUserId());

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

        User user = getLoggedInUser(principal);

        Boolean enabled = user.getNotificationsEnabled();

        if (enabled == null) {
            enabled = true;
        }

        model.addAttribute("notificationsEnabled", enabled);

        // If disabled → show nothing
        if (!enabled) {

            model.addAttribute("notifications", List.of());
            model.addAttribute("unreadCount", 0);

            return "notifications/notifications";
        }

        List<NotificationResponse> notifications;
        long unreadCount;

        if (type == null) {

            notifications =
                    notificationService.getUserNotifications(user.getUserId());

            unreadCount =
                    notificationService.getUnreadCount(user.getUserId());

        } else {

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
    // MARK AS READ
    // =========================
    @PostMapping("/read/{id}")
    public String markRead(@PathVariable Long id,
                           @RequestParam(required = false) NotificationType type) {

        notificationService.markAsRead(id);

        if (type == null) {
            return "redirect:/notifications";
        }

        return "redirect:/notifications/filter?type=" + type;
    }

    // =========================
    // MARK AS UNREAD
    // =========================
    @PostMapping("/unread/{id}")
    public String markUnread(@PathVariable Long id,
                             @RequestParam(required = false) NotificationType type) {

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

        notificationService.deleteNotification(id);

        return "redirect:/notifications";
    }

    // =========================
    // MARK ALL READ
    // =========================
    @PostMapping("/read-all")
    public String markAllRead(Principal principal) {

        User user = getLoggedInUser(principal);

        notificationService.markAllAsRead(user.getUserId());

        return "redirect:/notifications";
    }

    // =========================
    // ENABLE NOTIFICATIONS
    // =========================
    @PostMapping("/enable")
    public String enableNotifications(Principal principal){

        User user = getLoggedInUser(principal);

        user.setNotificationsEnabled(true);

        userRepository.save(user);

        return "redirect:/notifications";
    }

    // =========================
    // DISABLE NOTIFICATIONS
    // =========================
    @PostMapping("/disable")
    public String disableNotifications(Principal principal){

        User user = getLoggedInUser(principal);

        user.setNotificationsEnabled(false);

        userRepository.save(user);

        return "redirect:/notifications";
    }
}