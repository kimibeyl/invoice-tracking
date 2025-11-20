package com.capitec.invoicetracking.service.impl;

import com.capitec.invoicetracking.domain.entity.BillingAccount;
import com.capitec.invoicetracking.domain.repository.BillingAccountRepository;
import com.capitec.invoicetracking.model.request.BillingAccountSearchRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;


import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static javax.management.Query.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BillingAccountServiceImplTest {

    @Mock
    private BillingAccountRepository billingAccountRepository;

    @InjectMocks
    private BillingAccountServiceImpl billingAccountService;

    @Test
    void testSearchAccountHolders_NameFilter() {
        BillingAccount account = new BillingAccount();
        account.setBillingAccId(UUID.randomUUID());
        account.setName("John Doe");
        account.setPhone("123456789");
        account.setEmail("john.doe@example.com");
        account.setAddress("123 Main Street");

        Pageable pageable = PageRequest.of(0, 10);
        List<BillingAccount> accounts = Collections.singletonList(account);
        Page<BillingAccount> page = new PageImpl<>(accounts, pageable, 1);

        when(billingAccountRepository.findAll(Mockito.any(org.springframework.data.jpa.domain.Specification.class), eq(pageable))).thenReturn(page);

        BillingAccountSearchRequest request = new BillingAccountSearchRequest();
        request.setName("John");

        Page<BillingAccount> result = billingAccountService.searchAccountHolders(pageable, request);

        assertEquals(1, result.getTotalElements());
        assertEquals("John Doe", result.getContent().get(0).getName());
    }

    @Test
    void testSearchAccountHolders_PhoneFilter() {
        BillingAccount account = new BillingAccount();
        account.setBillingAccId(UUID.randomUUID());
        account.setName("Jane Doe");
        account.setPhone("987654321");
        account.setEmail("jane.doe@example.com");
        account.setAddress("456 Elm Street");

        Pageable pageable = PageRequest.of(0, 10);
        List<BillingAccount> accounts = Collections.singletonList(account);
        Page<BillingAccount> page = new PageImpl<>(accounts, pageable, 1);

        when(billingAccountRepository.findAll((Example<BillingAccount>) any(), Mockito.eq(pageable))).thenReturn(page);

        BillingAccountSearchRequest request = new BillingAccountSearchRequest();
        request.setPhone("987");

        Page<BillingAccount> result = billingAccountService.searchAccountHolders(pageable, request);

        assertEquals(1, result.getTotalElements());
        assertEquals("Jane Doe", result.getContent().get(0).getName());
    }

    @Test
    void testSearchAccountHolders_NoFilters() {
        BillingAccount account = new BillingAccount();
        account.setBillingAccId(UUID.randomUUID());
        account.setName("System User");
        account.setPhone("000000");
        account.setEmail("system@example.com");
        account.setAddress("System Default Address");
        account.setCreatedBy("SYSTEM");

        Pageable pageable = PageRequest.of(0, 10);
        List<BillingAccount> accounts = Collections.singletonList(account);
        Page<BillingAccount> page = new PageImpl<>(accounts, pageable, 1);

        when(billingAccountRepository.findAll((Example<BillingAccount>) any(), Mockito.eq(pageable))).thenReturn(page);

        BillingAccountSearchRequest request = new BillingAccountSearchRequest();

        Page<BillingAccount> result = billingAccountService.searchAccountHolders(pageable, request);

        assertEquals(1, result.getTotalElements());
        assertEquals("System User", result.getContent().get(0).getName());
    }

    @Test
    void testSearchAccountHolders_MultipleFilters() {
        BillingAccount account = new BillingAccount();
        account.setBillingAccId(UUID.randomUUID());
        account.setName("Multi Filter");
        account.setPhone("111222333");
        account.setEmail("multifilter@example.com");
        account.setAddress("789 Oak Street");

        Pageable pageable = PageRequest.of(0, 10);
        List<BillingAccount> accounts = Collections.singletonList(account);
        Page<BillingAccount> page = new PageImpl<>(accounts, pageable, 1);

        when(billingAccountRepository.findAll((Example<BillingAccount>) any(), Mockito.eq(pageable))).thenReturn(page);

        BillingAccountSearchRequest request = new BillingAccountSearchRequest();
        request.setName("Multi");
        request.setPhone("111");

        Page<BillingAccount> result = billingAccountService.searchAccountHolders(pageable, request);

        assertEquals(1, result.getTotalElements());
        assertEquals("Multi Filter", result.getContent().get(0).getName());
    }

    @Test
    void testSearchAccountHolders_NoResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BillingAccount> page = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(billingAccountRepository.findAll((Example<BillingAccount>) any(), Mockito.eq(pageable))).thenReturn(page);

        BillingAccountSearchRequest request = new BillingAccountSearchRequest();
        request.setName("NonExistingName");

        Page<BillingAccount> result = billingAccountService.searchAccountHolders(pageable, request);

        assertEquals(0, result.getTotalElements());
    }
}