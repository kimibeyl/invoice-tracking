package com.capitec.invoicetracking.config.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ToString
@ConfigurationProperties(prefix = "auth")
@Component
public class AuthProperties {
    @NotBlank
    private String token;
}
