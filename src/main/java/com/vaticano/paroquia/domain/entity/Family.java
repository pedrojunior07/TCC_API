package com.vaticano.paroquia.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "families")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("deleted_at IS NULL")
public class Family {

    @Id
    @Column(name = "family_id", nullable = false, length = 50)
    private String familyId;  // ULID com prefixo fam_

    @Column(name = "nome", nullable = false, length = 300)
    private String nome;

    @Column(name = "residencia", length = 500)
    private String residencia;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "nome_do_pai", length = 300)
    private String nomeDoPai;

    @Column(name = "nome_da_mae", length = 300)
    private String nomeDaMae;

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
