package com.capitec.invoicetracking.model.request;

import lombok.Data;

@Data
public class BillingAccountSearch {
    private String name;
    private String address;
    private String phone;
    private String email;
}
