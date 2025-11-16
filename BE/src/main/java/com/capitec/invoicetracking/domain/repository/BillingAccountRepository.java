package com.capitec.invoicetracking.domain.repository;

import com.capitec.invoicetracking.domain.entity.BillingAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface BillingAccountRepository extends JpaRepository<BillingAccount, UUID>, JpaSpecificationExecutor<BillingAccount> {}
