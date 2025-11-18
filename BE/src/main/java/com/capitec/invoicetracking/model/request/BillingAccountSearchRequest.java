package com.capitec.invoicetracking.model.request;

import lombok.Data;

@Data
public class BillingAccountSearchRequest {
    String name;
    String address;
    String phone;
    String email;
}
