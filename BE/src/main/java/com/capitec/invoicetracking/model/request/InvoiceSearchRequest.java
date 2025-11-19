package com.capitec.invoicetracking.model.request;

import com.capitec.invoicetracking.enums.InvoiceStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class InvoiceSearchRequest {
    private UUID invoiceId;
    private String accountName;
    private UUID billingAccountId;
    private String invoiceNumber;
    private LocalDateTime invoiceDate;
    private InvoiceStatus[] status;
    private BigDecimal amount;
}
