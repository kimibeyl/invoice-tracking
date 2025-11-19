package com.capitec.invoicetracking.controller;

import com.capitec.invoicetracking.configuration.TestSecurityConfig;
import com.capitec.invoicetracking.domain.entity.BillingAccount;
import com.capitec.invoicetracking.model.request.BillingAccountSearchRequest;
import com.capitec.invoicetracking.model.response.BillingAccountResponse;
import com.capitec.invoicetracking.service.BillingAccountService;
import com.capitec.invoicetracking.service.JWTUtility;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(BillingAccountController.class)
@Import({TestSecurityConfig.class})
public class BillingAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BillingAccountService billingService;

    @MockitoBean
    private ModelMapper modelMapper;

    @MockitoBean
    private JWTUtility jwtUtility;

    @Test
    void testSearchAccountReturnsEmptyResult() throws Exception {
        BillingAccountSearchRequest searchRequest = new BillingAccountSearchRequest();
        searchRequest.setName("TestName");

        Page<BillingAccount> emptyPage = new PageImpl<>(Collections.emptyList());
        Mockito.when(billingService.searchAccountHolders(any(Pageable.class), any(BillingAccountSearchRequest.class)))
                .thenReturn(emptyPage);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/billing/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"TestName\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

//    @Test
//    void testSearchAccountReturnsSingleResult() throws Exception {
//        BillingAccountSearchRequest searchRequest = new BillingAccountSearchRequest();
//        searchRequest.setName("TestName");
//
//        BillingAccount mockBillingAccount = new BillingAccount();
//        mockBillingAccount.setBillingAccId(UUID.randomUUID());
//        mockBillingAccount.setName("TestName");
//
//        BillingAccountResponse mockResponse = new BillingAccountResponse();
//        mockResponse.setBillingAccId(UUID.randomUUID());
//        mockResponse.setName("TestName");
//
//        Page<BillingAccount> singlePage = new PageImpl<>(Collections.singletonList(mockBillingAccount));
//        Mockito.when(billingService.searchAccountHolders(any(Pageable.class), any(BillingAccountSearchRequest.class)))
//                .thenReturn(singlePage);
//        Mockito.when(modelMapper.map(any(BillingAccount.class), any(Class.class))).thenReturn(mockResponse);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/billing/search")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"name\":\"TestName\"}")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content[0].id").value(1))
//                .andExpect(jsonPath("$.content[0].name").value("TestName"));
//    }

//    @Test
//    void testSearchAccountWithInvalidRequestBody() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/billing/search")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"invalidField\":\"InvalidValue\"}")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
}