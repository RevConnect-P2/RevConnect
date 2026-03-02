package com.revconnect.controller;

import com.revconnect.entity.Notification;
import com.revconnect.entity.User;
import com.revconnect.enums.NotificationType;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    // ===============================
    // VIEW ALL / FILTER NOTIFICATIONS
    // ===============================
    @GetMapping
    public String getNotifications(
            @RequestParam(required = false) NotificationType type,
            Model model,
            Principal principal) {

        User user = userRepository
                .findByEmail(principal.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Page<Notification> notifications;

        if (type == null) {
            notifications =
                    notificationService.getAll(user.getUserId(), 0, 20);
        } else {
            notifications =
                    notificationService.getByType(
                            user.getUserId(),
                            type,
                            0,
                            20);
        }

        model.addAttribute("notifications",
                notifications.getContent());

        // ✅ FIXED unread count
        model.addAttribute("unreadCount",
                notificationService.getUnreadCountByType(
                        user.getUserId(),
                        type));

        // ✅ send selected type back to UI
        model.addAttribute("selectedType", type);

        return "Notification/notifications";
    }

    // ===============================
    // MARK SINGLE AS READ
    // ===============================
    @PostMapping("/{id}/read")
    public String markRead(@PathVariable Long id,
                           Principal principal) {

        User user = userRepository
                .findByEmail(principal.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        notificationService.markAsRead(id,
                user.getUserId());

        return "redirect:/notifications";
    }

    // ===============================
    // MARK SINGLE AS UNREAD
    // ===============================
    @PostMapping("/{id}/unread")
    public String markUnread(@PathVariable Long id,
                             Principal principal) {

        User user = userRepository
                .findByEmail(principal.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        notificationService.markAsUnread(id,
                user.getUserId());

        return "redirect:/notifications";
    }

    // ===============================
    // MARK ALL AS READ
    // ===============================
    @PostMapping("/read-all")
    public String markAll(Principal principal) {

        User user = userRepository
                .findByEmail(principal.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        notificationService.markAllAsRead(
                user.getUserId());

        return "redirect:/notifications";
    }

    // ===============================
    // DELETE NOTIFICATION
    // ===============================
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         Principal principal) {

        User user = userRepository
                .findByEmail(principal.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        notificationService.delete(id,
                user.getUserId());

        return "redirect:/notifications";
    }

    @GetMapping("/test")
    @ResponseBody
    public String createTestNotification(Principal principal) {

        User user = userRepository
                .findByEmail(principal.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        notificationService.createNotification(
                user.getUserId(),   // receiver
                user.getUserId(),   // sender (self for test)
                101L,               // referenceId
                NotificationType.LIKE,
                "Test Like Notification"
        );

        return "Test notification created";
    }
}