package com.capitec.invoicetracking.controller;

import com.capitec.invoicetracking.configuration.TestSecurityConfig;
import com.capitec.invoicetracking.domain.entity.Invoice;
import com.capitec.invoicetracking.exception.ResourceNotFoundException;
import com.capitec.invoicetracking.model.request.InvoiceSearchRequest;
import com.capitec.invoicetracking.model.response.InvoiceResponse;
import com.capitec.invoicetracking.service.InvoiceService;
import com.capitec.invoicetracking.service.JWTUtility;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.View;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(InvoiceController.class)
@Import({TestSecurityConfig.class})
class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InvoiceService invoiceService;

    @MockitoBean
    private ModelMapper modelMapper;

    @MockitoBean
    private JWTUtility jwtUtility;
    @Autowired
    private View error;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetInvoice_Success() throws Exception {
        // Arrange
        UUID invoiceId = UUID.randomUUID();
        Invoice invoice = new Invoice();
        invoice.setInvoiceId(invoiceId);
        InvoiceResponse invoiceResponse = new InvoiceResponse();

        when(invoiceService.getInvoice(invoiceId)).thenReturn(invoice);
        when(modelMapper.map(invoice, InvoiceResponse.class)).thenReturn(invoiceResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/invoice/{invoiceId}", invoiceId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetInvoice_NotFound() throws Exception {
        // Arrange
        UUID invoiceId = UUID.randomUUID();

        when(invoiceService.getInvoice(invoiceId)).thenThrow(new ResourceNotFoundException("Invoice", invoiceId.toString()));

        // Act & Assert
        var result = mockMvc.perform(get("/api/v1/invoice/{invoiceId}", invoiceId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andReturn();

        assertTrue(result.getResponse().getContentAsString()
                .contains("Invoice with identity " + invoiceId + " not found"));
    }

//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void testSearchInvoices_Success() throws Exception {
//        // Arrange
//        InvoiceSearchRequest searchRequest = new InvoiceSearchRequest();
//        Invoice invoice = new Invoice();
//        Page<Invoice> pagedInvoices = new PageImpl<>(Collections.singletonList(invoice), PageRequest.of(0, 20), 1);
//        when(invoiceService.searchInvoices(any(Pageable.class), eq(searchRequest))).thenReturn(pagedInvoices);
//        // Map invoice to response (simulate ModelMapper)
//        InvoiceResponse invoiceResponse = new InvoiceResponse();
//        when(modelMapper.map(any(Invoice.class), eq(InvoiceResponse.class))).thenReturn(invoiceResponse);
//        when(pagedInvoices.map(any())).thenCallRealMethod(); // Use real method to avoid proxy errors
//
//        // Act & Assert
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/invoice/search")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{}"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
//    }

//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void testSearchInvoices_UnPaged() throws Exception {
//        // Arrange
//        InvoiceSearchRequest searchRequest = new InvoiceSearchRequest();
//        Invoice invoice = new Invoice();
//        Page<Invoice> pagedInvoices = new PageImpl<>(Collections.singletonList(invoice), PageRequest.of(0, 20), 1);
//        when(invoiceService.searchInvoices(any(Pageable.class), eq(searchRequest))).thenReturn(pagedInvoices);
//        InvoiceResponse invoiceResponse = new InvoiceResponse();
//        when(modelMapper.map(any(Invoice.class), eq(InvoiceResponse.class))).thenReturn(invoiceResponse);
//        when(pagedInvoices.map(any())).thenCallRealMethod(); // Use real method to avoid bytebuddy serialization
//
//        // Act & Assert
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/invoice/search")
//                        .param("unPaged", "true")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{}"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
//    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSearchInvoices_EmptyResult() throws Exception {
        InvoiceSearchRequest searchRequest = new InvoiceSearchRequest();
        Page<Invoice> emptyPage = Page.empty();
        when(invoiceService.searchInvoices(any(Pageable.class), eq(searchRequest))).thenReturn(emptyPage);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/invoice/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isEmpty());
    }
}