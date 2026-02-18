package com.vaticano.paroquia.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UlidGenerator {

    /**
     * Gera um ID único usando UUID.
     * Remove os hífens e converte para lowercase para formato compacto.
     */
    private String generateId() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    public String generateUserId() {
        return "usr_" + generateId();
    }

    public String generateMemberId() {
        return "mbr_" + generateId();
    }

    public String generateFamilyId() {
        return "fam_" + generateId();
    }

    public String generateNucleoId() {
        return "nucleo_" + generateId();
    }

    public String generateActivityId() {
        return "act_" + generateId();
    }

    public String generateCargoId() {
        return "cargo_" + generateId();
    }

    public String generateContribuicaoId() {
        return "cont_" + generateId();
    }

    public String generateImagemId() {
        return "img_" + generateId();
    }

    public String generateVisitaId() {
        return "visita_" + generateId();
    }

    public String generateCertificateRequestId() {
        return "certreq_" + generateId();
    }

    public String generateRefreshTokenId() {
        return "rt_" + generateId();
    }

    public String generateWhatsappNotificacaoId() {
        return "wanotif_" + generateId();
    }

    public String generateAuditLogId() {
        return "audit_" + generateId();
    }

    public String generateGenericId() {
        return generateId();
    }
}
