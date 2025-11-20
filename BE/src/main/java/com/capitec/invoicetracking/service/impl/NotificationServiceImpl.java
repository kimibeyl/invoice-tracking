package com.capitec.invoicetracking.service.impl;

import com.capitec.invoicetracking.domain.entity.Notification;
import com.capitec.invoicetracking.domain.repository.NotificationRepository;
import com.capitec.invoicetracking.enums.NotificationStatus;
import com.capitec.invoicetracking.model.request.NotificationSearchRequest;
import com.capitec.invoicetracking.service.NotificationService;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public List<Notification> getActiveNotificationsOfInvoice(UUID accountId) {
        return notificationRepository.findAllByInvoice_InvoiceId(accountId);
    }

    @Override
    public Page<Notification> getAllNotifications(NotificationSearchRequest request, Pageable pageable) {
        return notificationRepository.findAll((root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(request.getAccountId())) {
                predicates.add(cb.equal(root.get("billingAccount").get("billingAccId"), request.getAccountId()));
            }

            if (StringUtils.isNotBlank(request.getMessage())) {
                predicates.add(cb.like(cb.lower(root.get("message")), "%" + request.getMessage().toLowerCase() + "%"));
            }

            if (request.getType() != null) {
                predicates.add(cb.equal(root.get("type"), request.getType()));
            }

            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }

            if (request.getFromDate() != null && request.getToDate() != null) {
                Instant fromInstant = Instant.parse(request.getToDate() + "T23:59:59.999Z");
                long fromEpoch = fromInstant.getEpochSecond();

                Instant toInstant = Instant.parse(request.getToDate() + "T23:59:59.999Z");
                long toEpoch = toInstant.getEpochSecond();

                predicates.add(cb.between(root.get("createdAt"), fromEpoch, toEpoch));
            } else if (request.getFromDate() != null) {
                Instant fromInstant = Instant.parse(request.getToDate() + "T23:59:59.999Z");
                long fromEpoch = fromInstant.getEpochSecond();
                predicates.add(cb.between(root.get("createdAt"), fromEpoch, Instant.now().getEpochSecond()));
            }

            if (predicates.isEmpty()) {
                return null;
            }

            if (query == null) {
                return null;
            }

            return query.where(predicates.toArray(new Predicate[0])).getRestriction();

        }, pageable);
    }

    @Override
    public void markAsRead(UUID[] notificationIds) {
        List<Notification> notificationList = notificationRepository.findAllByNotificationIdIn(notificationIds);

        for(Notification notificationItem : notificationList) {
            notificationItem.setStatus(NotificationStatus.READ);
            notificationRepository.save(notificationItem);
        }
    }
}
