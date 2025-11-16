package com.capitec.invoicetracking.controller;

import com.capitec.invoicetracking.domain.entity.Invoice;
import com.capitec.invoicetracking.model.request.InvoiceCreateRequest;
import com.capitec.invoicetracking.model.request.InvoiceSearchRequest;
import com.capitec.invoicetracking.model.response.InvoiceResponse;
import com.capitec.invoicetracking.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invoice/")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Invoice Tracking API", description = "API to manage invoices")
public class InvoiceController {
    private final InvoiceService invoiceService;
    private final ModelMapper modelMapper;

    public InvoiceController(InvoiceService invoiceService, ModelMapper modelMapper) {
        this.invoiceService = invoiceService;
        this.modelMapper = modelMapper;
    }

    @Operation(summary = "Retrieves a paginating list of invoices.", description = "Returns invoice paginated summary.")
    @GetMapping(value = "/{invoiceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InvoiceResponse> getInvoice(@PathVariable UUID invoiceId) {

       Invoice invoice = invoiceService.getInvoice(invoiceId);

        return ResponseEntity.ok(modelMapper.map(invoice, InvoiceResponse.class));
    }

    @Operation(summary = "Retrieves a paginating list of invoices.", description = "Returns invoice paginated summary.")
    @PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<InvoiceResponse>> searchInvoices(Pageable pageable,
                                                                @RequestBody InvoiceSearchRequest searchRequest,
                                                                @RequestParam(required = false) Boolean unPaged) {
        if (Boolean.TRUE.equals(unPaged)) {
            pageable = PageRequest.of(0, 10000);
        }
        Page<Invoice> pagedInvoice = invoiceService.searchInvoices(pageable, searchRequest);

        return ResponseEntity.ok(pagedInvoice.map(invoice -> modelMapper.map(invoice, InvoiceResponse.class)));
    }

    @Operation(summary = "Create a new invoice.", description = "Creates an new invoice")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InvoiceResponse> createInvoices(@RequestBody InvoiceCreateRequest createRequest) {

        Invoice invoice = invoiceService.createInvoice(createRequest);

        return ResponseEntity.ok(modelMapper.map(invoice, InvoiceResponse.class));
    }

    @Operation(summary = "Create a new invoice.", description = "Creates an new invoice")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InvoiceResponse> updateInvoices(@RequestBody InvoiceCreateRequest createRequest) {

        Invoice invoice = invoiceService.updateInvoice(createRequest);

        return ResponseEntity.ok(modelMapper.map(invoice, InvoiceResponse.class));
    }
}
