package com.capitec.invoicetracking.model.request;

import com.capitec.invoicetracking.enums.NotificationStatus;
import com.capitec.invoicetracking.enums.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationSearchRequest {
    private String message;
    private String accountId;
    private NotificationType type;
    private NotificationStatus status;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
}
