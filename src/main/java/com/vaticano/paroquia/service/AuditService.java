package com.vaticano.paroquia.service;

import com.vaticano.paroquia.domain.entity.AuditLog;
import com.vaticano.paroquia.domain.repository.AuditLogRepository;
import com.vaticano.paroquia.security.SecurityUtils;
import com.vaticano.paroquia.util.UlidGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UlidGenerator ulidGenerator;
    private final SecurityUtils securityUtils;

    /**
     * Registra log de auditoria de forma assíncrona.
     *
     * @param tipo    Tipo do evento (ex: "user_created", "member_deleted", "import_csv")
     * @param mensagem Mensagem descritiva do evento
     * @param meta     Metadados adicionais em formato JSON (opcional)
     * @param entityId ID da entidade afetada (opcional)
     */
    @Async
    public void log(String tipo, String mensagem, Map<String, Object> meta, String entityId) {
        try {
            String userId = null;
            try {
                userId = securityUtils.getCurrentUserId();
            } catch (Exception e) {
                // Usuário não autenticado (ex: bootstrap), userId fica null
            }

            AuditLog auditLog = AuditLog.builder()
                    .id(ulidGenerator.generateAuditLogId())
                    .tipo(tipo)
                    .mensagem(mensagem)
                    .meta(meta)
                    .userId(userId)
                    .entityId(entityId)
                    .build();

            auditLogRepository.save(auditLog);

            log.debug("Audit log criado: tipo={}, mensagem={}", tipo, mensagem);
        } catch (Exception e) {
            // Não falhar operação principal por erro de auditoria
            log.error("Erro ao criar audit log: tipo={}, mensagem={}", tipo, mensagem, e);
        }
    }

    /**
     * Sobrecarga sem entityId.
     */
    @Async
    public void log(String tipo, String mensagem, Map<String, Object> meta) {
        log(tipo, mensagem, meta, null);
    }

    /**
     * Sobrecarga sem meta nem entityId.
     */
    @Async
    public void log(String tipo, String mensagem) {
        log(tipo, mensagem, null, null);
    }
}
