package com.capitec.invoicetracking.model.response;

import lombok.Data;

import java.util.UUID;

@Data
public class BillingAccountResponse {
    private UUID billingAccId;
    private String name;
    private String address;
    private String phone;
    private String email;
}
