package com.vaticano.paroquia.domain.entity;

import com.vaticano.paroquia.domain.enums.EstadoVisita;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "visitas_familiares",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_visita_nucleo_semana", columnNames = {"nucleo_id", "semana_ref"})
    },
    indexes = {
        @Index(name = "idx_visitas_nucleo_id", columnList = "nucleo_id"),
        @Index(name = "idx_visitas_family_id", columnList = "family_id"),
        @Index(name = "idx_visitas_semana_ref", columnList = "semana_ref")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("deleted_at IS NULL")
public class VisitaFamiliar {

    @Id
    @Column(name = "id", nullable = false, length = 50)
    private String id;  // ULID com prefixo visita_

    @Column(name = "semana_ref", nullable = false, length = 50)
    private String semanaRef;  // Identificador da semana (ex: "2026-W07")

    @Column(name = "family_id", nullable = false, length = 50)
    private String familyId;

    @Column(name = "nucleo_id", nullable = false, length = 50)
    private String nucleoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private EstadoVisita estado = EstadoVisita.PLANEADA;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Soft delete
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by", length = 50)
    private String deletedBy;
}
