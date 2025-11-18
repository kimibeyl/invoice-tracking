package com.capitec.invoicetracking.model.request;

import com.capitec.invoicetracking.enums.InvoiceStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class InvoiceSearchRequest {
    UUID invoiceId;
    String accountName;
    UUID billingAccountId;
    String invoiceNumber;
    LocalDateTime invoiceDate;
    InvoiceStatus[] status;
    BigDecimal amount;
}
