package com.capitec.invoicetracking.domain.repository;

import com.capitec.invoicetracking.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID>, JpaSpecificationExecutor<Notification>  {
    List<Notification> findAllByInvoice_InvoiceId(UUID invoiceId);
    List<Notification> findAllByNotificationIdIn(UUID[] accountId);
}
