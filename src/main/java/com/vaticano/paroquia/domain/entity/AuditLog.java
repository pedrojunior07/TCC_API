package com.vaticano.paroquia.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_logs_tipo", columnList = "tipo"),
    @Index(name = "idx_audit_logs_user_id", columnList = "user_id"),
    @Index(name = "idx_audit_logs_timestamp", columnList = "timestamp")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @Column(name = "id", nullable = false, length = 50)
    private String id;  // ULID com prefixo audit_

    @Column(name = "tipo", nullable = false, length = 100)
    private String tipo;  // Ex: "membro_add", "membro_update", "import_csv", "certificado_emitido"

    @Column(name = "mensagem", nullable = false, columnDefinition = "TEXT")
    private String mensagem;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "meta", columnDefinition = "jsonb")
    private Map<String, Object> meta;  // Dados adicionais em JSON

    @Column(name = "user_id", length = 50)
    private String userId;  // Pode ser null para ações do sistema

    @Column(name = "entity_id", length = 200)
    private String entityId;  // ID da entidade afetada (ex: memberKey, userId, etc)

    @Column(name = "timestamp", nullable = false)
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
