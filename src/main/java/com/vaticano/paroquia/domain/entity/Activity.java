package com.vaticano.paroquia.domain.entity;

import com.vaticano.paroquia.domain.enums.EstadoActividade;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "activities", indexes = {
    @Index(name = "idx_activities_nucleo_id", columnList = "nucleo_id"),
    @Index(name = "idx_activities_data", columnList = "data"),
    @Index(name = "idx_activities_estado", columnList = "estado")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("deleted_at IS NULL")
public class Activity {

    @Id
    @Column(name = "id", nullable = false, length = 50)
    private String id;  // ULID com prefixo act_

    @Column(name = "nucleo_id", nullable = false, length = 50)
    private String nucleoId;

    @Column(name = "titulo", nullable = false, length = 500)
    @Builder.Default
    private String titulo = "Encontro Semanal";

    @Column(name = "data", nullable = false)
    private LocalDate data;

    @Column(name = "hora_inicio", length = 10)
    private String horaInicio;  // Formato HH:MM

    @Column(name = "hora_fim", length = 10)
    private String horaFim;  // Formato HH:MM

    @Column(name = "local", length = 500)
    private String local;

    @Column(name = "agenda", columnDefinition = "TEXT")
    private String agenda;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private EstadoActividade estado = EstadoActividade.PLANEADA;

    @Column(name = "participantes_estimados")
    private Integer participantesEstimados;

    @Column(name = "participantes_presentes")
    private Integer participantesPresentes;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

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
