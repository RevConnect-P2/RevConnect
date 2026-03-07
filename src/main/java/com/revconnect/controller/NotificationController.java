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

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    // show notification count in navbar
    @ModelAttribute
    public void notificationCount(Model model, Principal principal){

        if(principal != null){

            String email = principal.getName();

            User user = userRepository.findByEmail(email)
                    .orElseThrow();

            long unreadCount =
                    notificationService.getUnreadCount(user.getUserId());

            model.addAttribute("unreadCount", unreadCount);
        }
    }

    // open notifications page
    @GetMapping
    public String viewNotifications(Model model, Principal principal){

        String email = principal.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("notifications",
                notificationService.getUserNotifications(user.getUserId()));

        return "notifications/notifications";
    }

    // filter notifications
    @GetMapping("/filter")
    public String filterNotifications(@RequestParam(required = false) NotificationType type,
                                      Model model,
                                      Principal principal) {

        String email = principal.getName();

        User user = userRepository.findByEmail(email).orElseThrow();

        List<NotificationResponse> notifications;

        long unreadCount;

        if (type == null) {

            notifications = notificationService.getUserNotifications(user.getUserId());

            unreadCount = notificationService.getUnreadCount(user.getUserId());

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
    // mark notification read
    @PostMapping("/read/{id}")
    public String markRead(@PathVariable Long id,
                           @RequestParam(required=false) NotificationType type){

        notificationService.markAsRead(id);

        if(type == null){
            return "redirect:/notifications";
        }

        return "redirect:/notifications/filter?type=" + type;
    }

    // delete notification
    @PostMapping("/delete/{id}")
    public String deleteNotification(@PathVariable Long id){

        notificationService.deleteNotification(id);

        return "redirect:/notifications";
    }
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard/dashboard";
    }

    @PostMapping("/unread/{id}")
    public String markUnread(@PathVariable Long id,
                             @RequestParam(required=false) NotificationType type){

        notificationService.markAsUnread(id);

        if(type == null){
            return "redirect:/notifications";
        }

        return "redirect:/notifications/filter?type=" + type;
    }

    @PostMapping("/read-all")
    public String markAllRead(Principal principal){

        String email = principal.getName();

        User user = userRepository.findByEmail(email).orElseThrow();

        notificationService.markAllAsRead(user.getUserId());

        return "redirect:/notifications";
    }

}