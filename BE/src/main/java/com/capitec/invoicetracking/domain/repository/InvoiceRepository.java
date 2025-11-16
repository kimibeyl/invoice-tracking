package com.capitec.invoicetracking.domain.repository;

import com.capitec.invoicetracking.domain.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository  extends JpaRepository<Invoice, UUID>, JpaSpecificationExecutor<Invoice> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
}
