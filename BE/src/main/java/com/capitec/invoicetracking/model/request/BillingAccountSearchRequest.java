package com.capitec.invoicetracking.model.request;

import lombok.Data;

@Data
public class BillingAccountSearchRequest {
    private String name;
    private String address;
    private String phone;
    private String email;
}
