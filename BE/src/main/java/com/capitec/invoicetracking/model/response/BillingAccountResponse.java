package com.capitec.invoicetracking.model.response;

import lombok.Data;

import java.util.UUID;

@Data
public class BillingAccountResponse {
    UUID billingAccId;
    String name;
    String address;
    String phone;
    String email;
}
