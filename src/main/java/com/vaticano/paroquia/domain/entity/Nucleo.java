package com.vaticano.paroquia.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "nucleos", indexes = {
    @Index(name = "idx_nucleos_ativo", columnList = "ativo")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("deleted_at IS NULL")
public class Nucleo {

    @Id
    @Column(name = "id", nullable = false, length = 50)
    private String id;  // ULID com prefixo nucleo_

    @Column(name = "nome", nullable = false, length = 300)
    private String nome;

    @Column(name = "comunidade", length = 200)
    private String comunidade;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "dia_encontro", length = 50)
    @Builder.Default
    private String diaEncontro = "Quarta-feira";

    @Column(name = "hora_encontro", length = 10)
    @Builder.Default
    private String horaEncontro = "19:00";

    @Column(name = "local_encontro", length = 500)
    private String localEncontro;

    @Column(name = "ativo", nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @Column(name = "whatsapp_group_id", length = 100)
    private String whatsappGroupId;

    @Column(name = "whatsapp_group_name", length = 300)
    private String whatsappGroupName;

    @Column(name = "whatsapp_invite_link", length = 500)
    private String whatsappInviteLink;

    // Relacionamentos Many-to-Many com Member
    @ElementCollection
    @CollectionTable(name = "nucleo_members", joinColumns = @JoinColumn(name = "nucleo_id"))
    @Column(name = "member_key", length = 200)
    @Builder.Default
    private Set<String> memberKeys = new HashSet<>();

    // Relacionamentos Many-to-Many com User (chefes de núcleo)
    @ElementCollection
    @CollectionTable(name = "nucleo_chefes", joinColumns = @JoinColumn(name = "nucleo_id"))
    @Column(name = "user_id", length = 50)
    @Builder.Default
    private Set<String> chefeUserIds = new HashSet<>();

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
