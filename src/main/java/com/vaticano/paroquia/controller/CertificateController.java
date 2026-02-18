package com.vaticano.paroquia.controller;

import com.vaticano.paroquia.dto.request.CertificateGenerateRequest;
import com.vaticano.paroquia.service.CertificateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Certificados", description = "Geracao de certificados em PDF e Word")
public class CertificateController {

    private final CertificateService certificateService;

    @PostMapping("/generate")
    @Operation(summary = "Gerar certificado", description = "Gera e devolve certificado em PDF ou Word (DOCX)")
    public ResponseEntity<byte[]> generateCertificate(@Valid @RequestBody CertificateGenerateRequest request) {
        CertificateService.GeneratedCertificate generated = certificateService.generate(request);

        String encodedFilename = URLEncoder.encode(generated.getFilename(), StandardCharsets.UTF_8)
                .replace("+", "%20");

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(generated.getContentType()))
                .body(generated.getBytes());
    }
}

