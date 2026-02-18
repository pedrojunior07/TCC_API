package com.vaticano.paroquia.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificateGenerateRequest {

    @NotBlank(message = "Tipo de certificado e obrigatorio")
    private String type;

    @NotBlank(message = "Formato de certificado e obrigatorio")
    private String format;

    @NotNull(message = "Dados do certificado sao obrigatorios")
    private Map<String, Object> data;

    private String memberKey;
    private String requestId;
}

