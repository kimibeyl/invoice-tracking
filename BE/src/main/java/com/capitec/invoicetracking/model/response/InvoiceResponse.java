package com.capitec.invoicetracking.model.response;

import com.capitec.invoicetracking.enums.InvoiceStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class InvoiceResponse {
    private UUID invoiceId;
    private String invoiceNumber;
    private UUID billingAccountId;
    private String billingAccountNumber;
    private String billingAccountName;
    private String billingAccountAddress;
    private String billingAccountEmail;
    private LocalDateTime invoiceDate;
    private InvoiceStatus status;
    private BigDecimal amount;
    private BigDecimal vatAmount;
}
