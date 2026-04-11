package com.talentsboard.backend.service;

import com.talentsboard.backend.dto.NotificationDTO;
import com.talentsboard.backend.model.Notification;
import com.talentsboard.backend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<NotificationDTO> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUserId(userId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<NotificationDTO> getUnreadByUser(Long userId) {
        return notificationRepository.findByUserIdAndIsRead(userId, false).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public long countUnread(Long userId) {
        return notificationRepository.countByUserIdAndIsRead(userId, false);
    }

    public NotificationDTO createNotification(Long userId, String message, String type) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setType(type);
        return toDTO(notificationRepository.save(notification));
    }

    public NotificationDTO markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Notification non trouvée: " + id));
        notification.setRead(true);
        return toDTO(notificationRepository.save(notification));
    }

    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findByUserIdAndIsRead(userId, false);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    private NotificationDTO toDTO(Notification n) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(n.getId());
        dto.setUserId(n.getUserId());
        dto.setMessage(n.getMessage());
        dto.setType(n.getType());
        dto.setRead(n.isRead());
        if (n.getCreatedAt() != null) dto.setCreatedAt(n.getCreatedAt().toString());
        return dto;
    }
}
