package com.capitec.invoicetracking.controller;


import com.capitec.invoicetracking.model.response.PingResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {
    private final BuildProperties buildProperties;
    public PingController(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;

    }
    @Operation(summary = "Pings the api", description = "Returns information about the api")
    @GetMapping(value = "/api/v1/ping", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PingResponse> ping() {
         return ResponseEntity.ok(new PingResponse(buildProperties.getName(),  buildProperties.getVersion()));
    }
}
