package com.capitec.invoicetracking.model.response;

import com.capitec.invoicetracking.enums.InvoiceStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class InvoiceResponse {
    UUID invoiceId;
    String invoiceNumber;
    UUID billingAccountId;
    String billingAccountNumber;
    String billingAccountName;
    String billingAccountAddress;
    String billingAccountEmail;
    LocalDateTime invoiceDate;
    InvoiceStatus status;
    BigDecimal amount;
    BigDecimal vatAmount;
}
