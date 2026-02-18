package com.vaticano.paroquia.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "whatsapp_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhatsappConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Singleton, sempre 1 registro

    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private Boolean enabled = false;

    @Column(name = "api_base_url", length = 500)
    private String apiBaseUrl;

    @Column(name = "token", length = 500)  // Deve ser encriptado
    private String token;

    @Column(name = "sender_id", length = 100)
    private String senderId;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
