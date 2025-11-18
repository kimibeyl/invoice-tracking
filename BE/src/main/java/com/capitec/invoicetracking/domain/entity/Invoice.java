package com.capitec.invoicetracking.domain.entity;

import com.capitec.invoicetracking.enums.InvoiceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import java.util.UUID;

@Entity
@Table(name = "invoice")
@Getter
@Setter
@NoArgsConstructor
public class Invoice extends AbstractBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "invoice_id")
    private UUID invoiceId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "billing_acc_id", nullable = false)
    private BillingAccount billingAccount;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InvoiceStatus status = InvoiceStatus.UNPAID;

    @Column(name = "invoice_date", nullable = false)
    private long invoiceDate;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "vat_amount")
    private BigDecimal vatAmount;
}
