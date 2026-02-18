package com.vaticano.paroquia.domain.entity;

import com.vaticano.paroquia.domain.enums.EstadoCargo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cargos", indexes = {
    @Index(name = "idx_cargos_nucleo_id", columnList = "nucleo_id"),
    @Index(name = "idx_cargos_estado", columnList = "estado")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("deleted_at IS NULL")
public class Cargo {

    @Id
    @Column(name = "id", nullable = false, length = 50)
    private String id;  // ULID com prefixo cargo_

    @Column(name = "nucleo_id", nullable = false, length = 50)
    private String nucleoId;

    @Column(name = "cargo", nullable = false, length = 200)
    private String cargo;

    @Column(name = "responsavel_nome", length = 300)
    private String responsavelNome;

    @Column(name = "responsavel_contacto", length = 100)
    private String responsavelContacto;

    @Column(name = "inicio_mandato")
    private LocalDate inicioMandato;

    @Column(name = "fim_mandato")
    private LocalDate fimMandato;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private EstadoCargo estado = EstadoCargo.ATIVO;

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
