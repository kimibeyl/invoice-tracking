package com.capitec.invoicetracking.model.request;

import com.capitec.invoicetracking.enums.InvoiceStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
public class InvoiceCreateRequest {
    String invoiceNumber;
    LocalDateTime invoiceDate;
    InvoiceStatus status;
    BigDecimal amount;
    BigDecimal vatAmount;
    UUID billingAccountId;
}