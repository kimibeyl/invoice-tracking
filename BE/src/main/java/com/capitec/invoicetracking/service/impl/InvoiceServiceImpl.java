package com.capitec.invoicetracking.service.impl;

import com.capitec.invoicetracking.domain.entity.BillingAccount;
import com.capitec.invoicetracking.domain.entity.Invoice;
import com.capitec.invoicetracking.domain.entity.Notification;
import com.capitec.invoicetracking.domain.repository.BillingAccountRepository;
import com.capitec.invoicetracking.domain.repository.InvoiceRepository;
import com.capitec.invoicetracking.domain.repository.NotificationRepository;
import com.capitec.invoicetracking.enums.InvoiceStatus;
import com.capitec.invoicetracking.enums.NotificationStatus;
import com.capitec.invoicetracking.enums.NotificationType;
import com.capitec.invoicetracking.exception.InvoiceException;
import com.capitec.invoicetracking.exception.ResourceNotFoundException;
import com.capitec.invoicetracking.model.request.InvoiceCreateRequest;
import com.capitec.invoicetracking.model.request.InvoiceSearchRequest;
import com.capitec.invoicetracking.service.InvoiceService;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final BillingAccountRepository billingAccountRepository;
    private final NotificationRepository notificationRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, BillingAccountRepository billingAccountRepository, NotificationRepository notificationRepository) {
        this.invoiceRepository = invoiceRepository;
        this.billingAccountRepository = billingAccountRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Page<Invoice> searchInvoices(Pageable pageable, InvoiceSearchRequest request) {
        return invoiceRepository.findAll((root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();
            if (request.getInvoiceId() != null) {
                predicates.add(cb.equal(root.get("invoiceId"), request.getInvoiceId()));
            }
            if (request.getBillingAccountId() != null) {
                predicates.add(cb.equal(root.get("billingAccount").get("billingAccId"), request.getBillingAccountId()));
            }
            if (StringUtils.isNotBlank(request.getAccountName())) {
                predicates.add(cb.like(cb.lower(root.get("billingAccount").get("name")), "%" + request.getAccountName().toLowerCase() + "%"));
            }
            if (StringUtils.isNotBlank(request.getInvoiceNumber())) {
                predicates.add(cb.like(cb.lower(root.get("invoiceNumber")), "%" + request.getInvoiceNumber().toLowerCase() + "%"));
            }
            if (request.getInvoiceDate() != null) {
                long dateTime = request.getInvoiceDate().atZone(ZoneId.systemDefault()).toEpochSecond();
                predicates.add(cb.between(root.get("invoiceDate"), dateTime, Instant.now().getEpochSecond()));
            }
            if (request.getStatus() != null) {
                if (request.getStatus().length > 0) {
                    List<Predicate> statusPredicates = new ArrayList<>();
                    for (InvoiceStatus status : request.getStatus()) {

                        statusPredicates.add(cb.or(cb.equal(root.get("status"), status)));
                    }
                    predicates.add(cb.or(statusPredicates.toArray(new Predicate[0])));
                }
            }
            if (request.getAmount() != null) {
                predicates.add(cb.equal(root.get("amount"), request.getAmount()));
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
    public Invoice getInvoice(UUID invoiceId) {
        return invoiceRepository.findById(invoiceId).orElseThrow(() ->
                new ResourceNotFoundException("Invoice", invoiceId.toString()));
    }

    @Override
    public Invoice createInvoice(InvoiceCreateRequest request) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(request.getInvoiceNumber()).orElse(null);

        if (invoice != null) {
            throw new InvoiceException(HttpStatusCode.valueOf(400), "Invoice Number already exists");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            throw new InvoiceException(HttpStatusCode.valueOf(400), "Invoice amount can not be 0 or Null");
        }

        BillingAccount billingAccount = billingAccountRepository.findById(request.getBillingAccountId()).orElseThrow(() ->
                new ResourceNotFoundException("Billing account ", request.getBillingAccountId().toString()));


        invoice = new Invoice();
        invoice.setInvoiceDate(Instant.now().getEpochSecond());
        invoice.setInvoiceNumber(request.getInvoiceNumber());
        invoice.setAmount(request.getAmount());
        invoice.setVatAmount(request.getVatAmount());
        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.setBillingAccount(billingAccount);
        return invoiceRepository.save(invoice);
    }

    @Override
    public Invoice updateInvoice(InvoiceCreateRequest request) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(request.getInvoiceNumber()).orElseThrow(() ->
                new ResourceNotFoundException("Invoice ", request.getBillingAccountId().toString()));

        long epoch = request.getInvoiceDate().toEpochSecond(ZoneOffset.UTC);

        invoice.setInvoiceDate(epoch);
        invoice.setInvoiceNumber(request.getInvoiceNumber());
        invoice.setAmount(request.getAmount());
        invoice.setVatAmount(request.getVatAmount());
        invoice.setStatus(request.getStatus());
        return invoiceRepository.save(invoice);
    }

    @Override
    @Transactional
    public void checkPaymentStatus() {

        List<Invoice> invoices = invoiceRepository.findAll();
        long now = Instant.now().getEpochSecond();

        for (Invoice invoice : invoices) {

            if (invoice.getStatus() == InvoiceStatus.PENDING ||
                    invoice.getStatus() == InvoiceStatus.UNPAID ||
                    invoice.getStatus() == InvoiceStatus.DUE) {

                long invoiceDate = invoice.getInvoiceDate();
                long daysOverdue = (now - invoiceDate) / 86400;

                if (daysOverdue >= 30) {

                    InvoiceStatus newStatus;
                    String message;

                    if (daysOverdue >= 90) {
                        newStatus = InvoiceStatus.OVER_DUE;
                        message = "Invoice" + invoice.getInvoiceNumber() + " 90 days Overdue";
                    } else if (daysOverdue >= 60) {
                        newStatus = InvoiceStatus.DUE;
                        message = "Invoice" + invoice.getInvoiceNumber() + " 60 days Overdue";
                    } else {
                        newStatus = InvoiceStatus.UNPAID;
                        message = "Invoice" + invoice.getInvoiceNumber() + " 30 days Overdue";
                    }

                    // Update and flush invoice immediately
                    invoice.setStatus(newStatus);
                    Invoice savedInvoice = invoiceRepository.saveAndFlush(invoice);

                    // Create notification with the freshly saved invoice
                    Notification notification = new Notification();
                    notification.setType(NotificationType.IMPORTANT);
                    notification.setStatus(NotificationStatus.UNREAD);
                    notification.setInvoice(savedInvoice);
                    notification.setMessage(message);

                    notificationRepository.save(notification);
                }
            }
        }
    }
}
