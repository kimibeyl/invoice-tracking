package com.capitec.invoicetracking.model.response;

import com.capitec.invoicetracking.enums.NotificationStatus;
import com.capitec.invoicetracking.enums.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class NotificationResponse {
    UUID notificationId;
    NotificationType type;
    String accountName;
    String message;
    UUID invoiceId;
    String invoiceNumber;
    LocalDateTime invoiceDate;
    NotificationStatus status;
    LocalDateTime createdAt;
}
