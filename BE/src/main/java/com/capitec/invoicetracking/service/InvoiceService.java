package com.capitec.invoicetracking.service;

import com.capitec.invoicetracking.domain.entity.Invoice;
import com.capitec.invoicetracking.model.request.InvoiceCreateRequest;
import com.capitec.invoicetracking.model.request.InvoiceSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface InvoiceService {
    Page<Invoice> searchInvoices(Pageable pageable, InvoiceSearchRequest request);
    Invoice getInvoice(UUID invoiceId);
    Invoice createInvoice(InvoiceCreateRequest request);
    Invoice updateInvoice(InvoiceCreateRequest request);
    void checkPaymentStatus();
}
