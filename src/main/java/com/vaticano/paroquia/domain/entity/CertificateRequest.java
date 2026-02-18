package com.vaticano.paroquia.domain.entity;

import com.vaticano.paroquia.domain.enums.EstadoCertificado;
import com.vaticano.paroquia.domain.enums.TipoCertificado;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "certificate_requests", indexes = {
    @Index(name = "idx_cert_requests_member_key", columnList = "member_key"),
    @Index(name = "idx_cert_requests_nucleo_id", columnList = "nucleo_id"),
    @Index(name = "idx_cert_requests_estado", columnList = "estado")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateRequest {

    @Id
    @Column(name = "id", nullable = false, length = 50)
    private String id;  // ULID com prefixo certreq_

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoCertificado tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private EstadoCertificado estado = EstadoCertificado.PENDENTE;

    @Column(name = "member_key", nullable = false, length = 200)
    private String memberKey;

    @Column(name = "nucleo_id", nullable = false, length = 50)
    private String nucleoId;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "requested_by_user_id", nullable = false, length = 50)
    private String requestedByUserId;

    @Column(name = "processed_by_user_id", length = 50)
    private String processedByUserId;

    @Column(name = "motivo_recusa", columnDefinition = "TEXT")
    private String motivoRecusa;  // Caso seja recusado

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
