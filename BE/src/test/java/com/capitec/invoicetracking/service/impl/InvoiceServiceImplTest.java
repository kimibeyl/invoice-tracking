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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class InvoiceServiceImplTest {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private InvoiceServiceImpl invoiceService;

    @Autowired
    private BillingAccountRepository billingAccountRepository;

    @BeforeEach
    void cleanDB() {
        notificationRepository.deleteAll();
        invoiceRepository.deleteAll();
        billingAccountRepository.deleteAll();
        // Flush to ensure deletes are applied before test runs
        notificationRepository.flush();
        invoiceRepository.flush();
        billingAccountRepository.flush();
    }


    // --------------------------------------------------------------------
    // 30 DAYS OVERDUE
    // --------------------------------------------------------------------
    @Test
    void testCheckPaymentStatus_30DaysOverdue() {
        // Create and save a BillingAccount first
        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setName("Test Account 001");
        billingAccount.setEmail("test001@example.com");
        BillingAccount savedBillingAccount = billingAccountRepository.saveAndFlush(billingAccount);

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("INV-001");
        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.setInvoiceDate(Instant.now().minusSeconds(30 * 24 * 3600).getEpochSecond());
        invoice.setAmount(BigDecimal.valueOf(1000));
        invoice.setBillingAccount(savedBillingAccount);

        Invoice savedInvoice = invoiceRepository.saveAndFlush(invoice);

        invoiceService.checkPaymentStatus();

        // Re-fetch invoice to avoid stale object
        Invoice updated = invoiceRepository.findById(savedInvoice.getInvoiceId()).orElseThrow();
        assertEquals(InvoiceStatus.UNPAID, updated.getStatus());

        List<Notification> notifications = notificationRepository.findAll();
        assertEquals(1, notifications.size());
        Notification n = notifications.get(0);
        assertEquals(NotificationType.IMPORTANT, n.getType());
        assertEquals(NotificationStatus.UNREAD, n.getStatus());
        assertEquals("InvoiceINV-001 30 days Overdue", n.getMessage());
        assertEquals(updated.getInvoiceId(), n.getInvoice().getInvoiceId());
    }
    // --------------------------------------------------------------------
    // 60 DAYS OVERDUE
    // --------------------------------------------------------------------
    @Test
    void testCheckPaymentStatus_60DaysOverdue() {
        // Create and save a BillingAccount first
        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setName("Test Account 001");
        billingAccount.setEmail("test001@example.com");
        BillingAccount savedBillingAccount = billingAccountRepository.saveAndFlush(billingAccount);

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("INV-002");
        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.setInvoiceDate(Instant.now().minusSeconds(60 * 24 * 3600).getEpochSecond());
        invoice.setAmount(BigDecimal.valueOf(1500));
        invoice.setBillingAccount(savedBillingAccount);

        Invoice savedInvoice = invoiceRepository.saveAndFlush(invoice);

        invoiceService.checkPaymentStatus();

        List<Notification> notifications = notificationRepository.findAll();
        assertEquals(1, notifications.size());

        Notification n = notifications.get(0);
        assertEquals(NotificationType.IMPORTANT, n.getType());
        assertEquals(NotificationStatus.UNREAD, n.getStatus());
        assertEquals("InvoiceINV-002 60 days Overdue", n.getMessage());
        assertEquals(savedInvoice.getInvoiceId(), n.getInvoice().getInvoiceId());

        Invoice updated = invoiceRepository.findById(savedInvoice.getInvoiceId()).orElseThrow();
        assertEquals(InvoiceStatus.DUE, updated.getStatus());
    }

    // --------------------------------------------------------------------
    // 90 DAYS OVERDUE
    // --------------------------------------------------------------------
    @Test
    void testCheckPaymentStatus_90DaysOverdue() {
        // Create and save a BillingAccount first
        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setName("Test Account 001");
        billingAccount.setEmail("test001@example.com");
        BillingAccount savedBillingAccount = billingAccountRepository.saveAndFlush(billingAccount);

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("INV-003");
        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.setInvoiceDate(Instant.now().minusSeconds(90 * 24 * 3600).getEpochSecond());
        invoice.setAmount(BigDecimal.valueOf(2000));
        invoice.setBillingAccount(savedBillingAccount);

        Invoice savedInvoice = invoiceRepository.saveAndFlush(invoice);

        invoiceService.checkPaymentStatus();

        Invoice updated = invoiceRepository.findById(savedInvoice.getInvoiceId()).orElseThrow();
        assertEquals(InvoiceStatus.OVER_DUE, updated.getStatus());

        List<Notification> notifications = notificationRepository.findAll();
        assertEquals(1, notifications.size());

        Notification n = notifications.get(0);
        assertEquals(NotificationType.IMPORTANT, n.getType());
        assertEquals(NotificationStatus.UNREAD, n.getStatus());
        assertEquals("InvoiceINV-003 90 days Overdue", n.getMessage());
        assertEquals(savedInvoice.getInvoiceId(), n.getInvoice().getInvoiceId());
    }

    // --------------------------------------------------------------------
    // PAID INVOICE (NO NOTIFICATION)
    // --------------------------------------------------------------------
    @Test
    void testCheckPaymentStatus_NoNotificationForPaidInvoice() {
        // Create and save a BillingAccount first
        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setName("Test Account 001");
        billingAccount.setEmail("test001@example.com");
        BillingAccount savedBillingAccount = billingAccountRepository.saveAndFlush(billingAccount);

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("INV-004");
        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setInvoiceDate(Instant.now().minusSeconds(10 * 24 * 3600).getEpochSecond());
        invoice.setAmount(BigDecimal.valueOf(3000));
        invoice.setBillingAccount(savedBillingAccount);

        Invoice savedInvoice = invoiceRepository.saveAndFlush(invoice);

        invoiceService.checkPaymentStatus();

        assertEquals(0, notificationRepository.count());

        Invoice updated = invoiceRepository.findById(savedInvoice.getInvoiceId()).orElseThrow();
        assertEquals(InvoiceStatus.PAID, updated.getStatus());
    }

    // --------------------------------------------------------------------
    // CREATE INVOICE TESTS
    // --------------------------------------------------------------------
    @Test
    void testCreateInvoice_SuccessfulCreation() {
        // First, create and save the BillingAccount
        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setName("Test Account");
        billingAccount.setEmail("test@example.com");
        BillingAccount savedBillingAccount = billingAccountRepository.saveAndFlush(billingAccount);

        // Now create the invoice request with the saved billing account ID
        InvoiceCreateRequest request = new InvoiceCreateRequest();
        request.setInvoiceNumber("INV-005");
        request.setAmount(BigDecimal.valueOf(5000));
        request.setVatAmount(BigDecimal.valueOf(750));
        request.setBillingAccountId(savedBillingAccount.getBillingAccId());

        Invoice createdInvoice = invoiceService.createInvoice(request);

        assertNotNull(createdInvoice.getInvoiceId());
        assertEquals("INV-005", createdInvoice.getInvoiceNumber());
        assertEquals(BigDecimal.valueOf(5000), createdInvoice.getAmount());
        assertEquals(BigDecimal.valueOf(750), createdInvoice.getVatAmount());
        assertEquals(InvoiceStatus.PENDING, createdInvoice.getStatus());
    }

    @Test
    void testCreateInvoice_InvoiceNumberAlreadyExists() {
        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setName("Test Account");
        billingAccount.setEmail("test@example.com");
        BillingAccount savedBillingAccount = billingAccountRepository.saveAndFlush(billingAccount);

        Invoice existingInvoice = new Invoice();
        existingInvoice.setInvoiceNumber("INV-006");
        existingInvoice.setAmount(BigDecimal.valueOf(4000));
        existingInvoice.setBillingAccount(savedBillingAccount);

        invoiceRepository.save(existingInvoice);

        InvoiceCreateRequest request = new InvoiceCreateRequest();
        request.setInvoiceNumber("INV-006");
        request.setAmount(BigDecimal.valueOf(4000));

        InvoiceException exception = assertThrows(InvoiceException.class, () -> invoiceService.createInvoice(request));
        assertEquals("400 BAD_REQUEST " + "\"Invoice Number already exists\"", exception.getMessage());
    }

    @Test
    void testCreateInvoice_InvalidBillingAccount() {
        InvoiceCreateRequest request = new InvoiceCreateRequest();
        request.setInvoiceNumber("INV-007");
        request.setAmount(BigDecimal.valueOf(7000));
        request.setBillingAccountId(UUID.randomUUID()); // Invalid billing account ID

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> invoiceService.createInvoice(request));
        assertEquals("Billing account  with identity " +request.getBillingAccountId() + " not found!" , exception.getMessage());

    }

    @Test
    void testCreateInvoice_InvalidAmount() {
        InvoiceCreateRequest request = new InvoiceCreateRequest();
        request.setInvoiceNumber("INV-008");
        request.setAmount(BigDecimal.ZERO); // Invalid amount
        request.setBillingAccountId(UUID.randomUUID());

        InvoiceException exception = assertThrows(InvoiceException.class, () -> invoiceService.createInvoice(request));
        assertEquals("400 BAD_REQUEST " + "\"Invoice amount can not be 0 or Null\"", exception.getMessage());
    }
}
