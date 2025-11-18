package com.capitec.invoicetracking.model.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class PingResponse {
    private String systemName;
    private String version;
}
