package com.vaticano.paroquia.domain.entity;

import com.vaticano.paroquia.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_username_norm", columnList = "username_norm"),
    @Index(name = "idx_users_role", columnList = "role"),
    @Index(name = "idx_users_deleted_at", columnList = "deleted_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("deleted_at IS NULL")
public class User {

    @Id
    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;  // ULID com prefixo usr_

    @Column(name = "username", unique = true, nullable = false, length = 100)
    private String username;

    @Column(name = "username_norm", nullable = false, length = 100)
    private String usernameNorm;  // Versão normalizada para busca case-insensitive

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "password_hash", nullable = false, length = 256)
    private String passwordHash;

    @Column(name = "salt", nullable = false, length = 128)
    private String salt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by", length = 50)
    private String deletedBy;

    @PrePersist
    @PreUpdate
    public void normalizeUsername() {
        if (this.username != null) {
            this.usernameNorm = this.username.toLowerCase().trim();
        }
    }
}
