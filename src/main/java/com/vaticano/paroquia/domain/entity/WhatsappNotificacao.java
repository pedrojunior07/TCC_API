package com.vaticano.paroquia.domain.entity;

import com.vaticano.paroquia.domain.enums.TriggerNotificacao;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "whatsapp_notificacoes", indexes = {
    @Index(name = "idx_whatsapp_notif_nucleo_id", columnList = "nucleo_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhatsappNotificacao {

    @Id
    @Column(name = "id", nullable = false, length = 50)
    private String id;  // ULID com prefixo wanotif_

    @Column(name = "nucleo_id", nullable = false, length = 50)
    private String nucleoId;

    @Column(name = "nome", nullable = false, length = 200)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_type", nullable = false, length = 30)
    private TriggerNotificacao trigger;

    @Column(name = "template", nullable = false, columnDefinition = "TEXT")
    private String template;  // Com variáveis: {nucleo} {titulo} {data} {hora} {local}

    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
