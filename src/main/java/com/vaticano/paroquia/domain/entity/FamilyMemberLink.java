package com.vaticano.paroquia.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "family_member_links", indexes = {
    @Index(name = "idx_family_member_links_family_id", columnList = "family_id"),
    @Index(name = "idx_family_member_links_member_key", columnList = "member_key")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FamilyMemberLink {

    @Id
    @Column(name = "id", nullable = false, length = 50)
    private String id;  // ULID

    @Column(name = "family_id", nullable = false, length = 50)
    private String familyId;

    @Column(name = "member_key", nullable = false, length = 200)
    private String memberKey;

    @Column(name = "relacao", length = 100)
    private String relacao;  // Ex: "pai", "filho", "mae"
}
