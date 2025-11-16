package com.capitec.invoicetracking.config;

import com.capitec.invoicetracking.domain.entity.BillingAccount;
import com.capitec.invoicetracking.domain.entity.Invoice;
import com.capitec.invoicetracking.domain.entity.Notification;
import com.capitec.invoicetracking.model.response.BillingAccountResponse;
import com.capitec.invoicetracking.model.response.InvoiceResponse;
import com.capitec.invoicetracking.model.response.NotificationResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Not;
import org.modelmapper.Conditions;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;


@Configuration
@Slf4j
public class MapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        var modelMapper = new ModelMapper();

        // Configure epoch seconds to LocalDateTime converter
        Converter<Long, LocalDateTime> epochToLocalDateTime = context -> {
            Long epochSeconds = context.getSource();
            if (epochSeconds == null) return null;
            return LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneId.systemDefault());
        };

        // Configure LocalDateTime to epoch seconds converter
        Converter<LocalDateTime, Long> localDateTimeToEpoch = context -> {
            LocalDateTime dateTime = context.getSource();
            if (dateTime == null) return null;
            return dateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
        };

        // Register the converters
        modelMapper.addConverter(epochToLocalDateTime);
        modelMapper.addConverter(localDateTimeToEpoch);

        modelMapper.getConfiguration()
                .setPropertyCondition(Conditions.isNotNull())
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);

        modelMapper.createTypeMap(Invoice.class, InvoiceResponse.class)
                .addMapping(src -> src.getBillingAccount().getBillingAccId(), InvoiceResponse::setBillingAccountId)
                .addMapping(src -> src.getBillingAccount().getPhone(), InvoiceResponse::setBillingAccountNumber)
                .addMapping(src -> src.getBillingAccount().getAddress(), InvoiceResponse::setBillingAccountAddress)
                .addMapping(src -> src.getBillingAccount().getName(), InvoiceResponse::setBillingAccountName)
                .addMapping(src -> src.getBillingAccount().getEmail(), InvoiceResponse::setBillingAccountEmail);

        modelMapper.typeMap(Invoice.class, InvoiceResponse.class).addMappings(mapper ->
                mapper.using(epochToLocalDateTime).map(Invoice::getInvoiceDate, InvoiceResponse::setInvoiceDate));

        modelMapper.createTypeMap(Notification.class, NotificationResponse.class)
                .addMapping(src -> src.getInvoice().getBillingAccount().getName(), NotificationResponse::setAccountName)
                .addMapping(src -> src.getInvoice().getInvoiceId(), NotificationResponse::setInvoiceId)
                .addMapping(src -> src.getInvoice().getInvoiceNumber(), NotificationResponse::setInvoiceNumber);

        modelMapper.typeMap(Notification.class, NotificationResponse.class)
                .addMappings(mapper -> mapper.using(epochToLocalDateTime).map(Notification::getCreatedAt, NotificationResponse::setCreatedAt));

        modelMapper.createTypeMap(BillingAccount.class, BillingAccountResponse.class);

        return modelMapper;
    }

    @Bean
    public ObjectMapper objectMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setTimeZone(TimeZone.getTimeZone("UTC"));
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

        return objectMapper;
    }

}
