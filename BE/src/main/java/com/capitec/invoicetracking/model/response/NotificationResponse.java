package com.capitec.invoicetracking.model.response;

import com.capitec.invoicetracking.enums.NotificationStatus;
import com.capitec.invoicetracking.enums.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class NotificationResponse {
    private UUID notificationId;
    private NotificationType type;
    private String accountName;
    private String message;
    private UUID invoiceId;
    private String invoiceNumber;
    private LocalDateTime invoiceDate;
    private NotificationStatus status;
    private LocalDateTime createdAt;
}
