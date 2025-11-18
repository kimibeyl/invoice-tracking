package com.capitec.invoicetracking.schedule;

import com.capitec.invoicetracking.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final InvoiceService invoiceServiceImpl;

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000) // 24 hours
    public void syncPaymentStatus() throws Exception {
        invoiceServiceImpl.checkPaymentStatus();
    }
}
