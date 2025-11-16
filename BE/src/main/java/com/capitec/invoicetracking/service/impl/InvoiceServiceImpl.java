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

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
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
            if(request.getAmount() != null){
                predicates.add(cb.equal(root.get("amount"), request.getAmount()));
            }

            if (predicates.isEmpty()) {
               predicates.add(cb.like(cb.upper(root.get("createdBy")), "SYSTEM"));
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

        if(invoice != null)
        {
            throw new InvoiceException(HttpStatusCode.valueOf(400),"Invoice Number already exists");
        }

        if(request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) == 0)
        {
            throw new InvoiceException(HttpStatusCode.valueOf(400),"Invoice amount can not be 0 or Null");
        }

        BillingAccount billingAccount = billingAccountRepository.findById(request.getBillingAccountId()).orElseThrow(() ->
                new ResourceNotFoundException("Billing account ", request.getBillingAccountId().toString()));


        invoice = new Invoice();
        invoice.setCreatedAt(Instant.now().getEpochSecond());
        invoice.setInvoiceNumber(request.getInvoiceNumber());
        invoice.setAmount(request.getAmount());
        invoice.setVatAmount(request.getVatAmount());
        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.setBillingAccount(billingAccount);
        invoice.setCreatedAt(Instant.now().getEpochSecond());
        invoice.setCreatedBy("SYSTEM");
        invoice.setModifiedAt(Instant.now().getEpochSecond());
        invoice.setModifiedBy("SYSTEM");
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
        invoice.setCreatedBy("SYSTEM");
        invoice.setModifiedAt(Instant.now().getEpochSecond());
        invoice.setModifiedBy("SYSTEM");
        return invoiceRepository.save(invoice);
    }

    @Override
    public void checkPaymentStatus() {
        List<Invoice> invoiceList = invoiceRepository.findAll().stream().toList();
        for(Invoice invoice : invoiceList)
        {
            if(invoice.getStatus() == InvoiceStatus.PENDING || invoice.getStatus() == InvoiceStatus.UNPAID || invoice.getStatus() == InvoiceStatus.DUE)
            {
                long now = invoice.getInvoiceDate();
                var thirtyDates = Instant.now().plus(30, ChronoUnit.DAYS).getEpochSecond();
                var sixtyDates = Instant.now().plus(60, ChronoUnit.DAYS).getEpochSecond();
                var ninetyDates = Instant.now().plus(90, ChronoUnit.DAYS).getEpochSecond();

                if(thirtyDates >= now)
                {
                    Notification notification = new Notification();
                    notification.setType(NotificationType.IMPORTANT);
                    notification.setStatus(NotificationStatus.UNREAD);
                    notification.setInvoice(invoice);
                    notification.setCreatedAt(Instant.now().getEpochSecond());
                    notification.setCreatedBy("SYSTEM");
                    notification.setModifiedAt(Instant.now().getEpochSecond());
                    notification.setModifiedBy("SYSTEM");

                    if(sixtyDates >= now ) {
                        if(ninetyDates >= now) {
                            invoice.setStatus(InvoiceStatus.OVER_DUE);
                            notification.setMessage("Invoice" + invoice.getInvoiceNumber() + " 90 days Overdue");
                        } else {
                            invoice.setStatus(InvoiceStatus.DUE);
                            notification.setMessage("Invoice" + invoice.getInvoiceNumber() + " 60 days Overdue");
                        }
                    }
                    else {
                        invoice.setStatus(InvoiceStatus.UNPAID);
                        notification.setMessage("Invoice" + invoice.getInvoiceNumber() + " 30 days Overdue");
                    }
                    notificationRepository.save(notification);
                }
            }
        }
    }
}
