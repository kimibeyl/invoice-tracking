package com.capitec.invoicetracking.service;

import com.capitec.invoicetracking.domain.entity.BillingAccount;
import com.capitec.invoicetracking.model.request.BillingAccountSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BillingAccountService {
    Page<BillingAccount> searchAccountHolders(Pageable pageable, BillingAccountSearch request);
}
