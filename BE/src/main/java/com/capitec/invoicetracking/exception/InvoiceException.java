package com.capitec.invoicetracking.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

@Getter
@Setter
public class InvoiceException extends ResponseStatusException {
    public InvoiceException(HttpStatusCode errorCode, String message) {
        super(errorCode, message);
    }
}
