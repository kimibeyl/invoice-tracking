package com.capitec.invoicetracking.controller;

import com.capitec.invoicetracking.model.request.NotificationSearchRequest;
import com.capitec.invoicetracking.model.response.NotificationResponse;
import com.capitec.invoicetracking.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.capitec.invoicetracking.domain.entity.Notification;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/notification/")
@Tag(name = "Notification API", description = "API to manage notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final ModelMapper modelMapper;

    public NotificationController(NotificationService notificationService, ModelMapper modelMapper) {
        this.notificationService = notificationService;
        this.modelMapper = modelMapper;
    }

    @Operation(summary = "Retrieves a billing notifications of a account holder.", description = "Returns a list of notifications of an account holder.")
    @GetMapping(value = "/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NotificationResponse> getNotificationsOfAccountHolder(@PathVariable UUID accountId) {

        List<Notification> notification = notificationService.getActiveNotificationsOfInvoice(accountId);

        return ResponseEntity.ok(modelMapper.map(notification, NotificationResponse.class));
    }

    @Operation(summary = "Retrieves all notifications", description = "Returns a list of notifications.")
    @PostMapping(value = "/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<NotificationResponse>> getAllNotifications(@RequestBody NotificationSearchRequest request, Pageable pageable) {

        Page<Notification> pagedNotification = notificationService.getAllNotifications(request, pageable);
        return ResponseEntity.ok(pagedNotification.map(notification -> modelMapper.map(notification, NotificationResponse.class)));
    }

    @PostMapping(value = "/mark-read", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> markNotificationsAsRead(@RequestBody UUID[] notificationIds) {

        notificationService.markAsRead(notificationIds);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
