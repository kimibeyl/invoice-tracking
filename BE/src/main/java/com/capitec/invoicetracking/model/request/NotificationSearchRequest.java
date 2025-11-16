package com.capitec.invoicetracking.model.request;

import com.capitec.invoicetracking.enums.NotificationStatus;
import com.capitec.invoicetracking.enums.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationSearchRequest {
    String message;
    String accountId;
    NotificationType type;
    NotificationStatus status;
    LocalDateTime fromDate;
    LocalDateTime toDate;
}
