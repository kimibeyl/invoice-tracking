package com.capitec.invoicetracking.service;

import com.capitec.invoicetracking.domain.entity.Notification;
import com.capitec.invoicetracking.model.request.NotificationSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    List<Notification> getActiveNotificationsOfInvoice(UUID accountId);
    Page<Notification> getAllNotifications(NotificationSearchRequest request, Pageable pageable);
    void markAsRead(UUID[] notificationId);
}
