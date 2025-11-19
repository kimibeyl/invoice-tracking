package com.capitec.invoicetracking.controller;

import com.capitec.invoicetracking.domain.entity.BillingAccount;
import com.capitec.invoicetracking.model.request.BillingAccountSearchRequest;
import com.capitec.invoicetracking.model.response.BillingAccountResponse;
import com.capitec.invoicetracking.service.BillingAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/billing")
@Tag(name = "Billing Account API", description = "API to manage account holders")
public class BillingAccountController {
    private final BillingAccountService billingService;
    private final ModelMapper modelMapper;

    public BillingAccountController(BillingAccountService billingService, ModelMapper modelMapper) {
        this.billingService = billingService;
        this.modelMapper = modelMapper;
    }

    @Operation(summary = "Retrieves a paginating list of account holders.", description = "Returns account holder paginated summary.")
    @PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<BillingAccountResponse>> searchAccount(@RequestBody BillingAccountSearchRequest searchRequest,Pageable pageable) {

        Page<BillingAccount> pagedAccount = billingService.searchAccountHolders(pageable, searchRequest);

        return ResponseEntity.ok(pagedAccount.map(accountHolders -> modelMapper.map(accountHolders, BillingAccountResponse.class)));
    }
}
