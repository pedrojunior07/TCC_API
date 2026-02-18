package com.vaticano.paroquia.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "members", indexes = {
    @Index(name = "idx_members_nome_completo", columnList = "nome_completo"),
    @Index(name = "idx_members_comunidade", columnList = "comunidade"),
    @Index(name = "idx_members_deleted_at", columnList = "deleted_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("deleted_at IS NULL")
public class Member {

    @Id
    @Column(name = "member_key", nullable = false, length = 200)
    private String memberKey;  // Chave derivada via normalização

    @Column(name = "member_id", unique = true, nullable = false, length = 50)
    private String memberId;  // ULID com prefixo mbr_

    // Campo 1: Ord. (armazenado como string, não é o ID principal)
    @Column(name = "ord_original", length = 50)
    private String ordOriginal;

    // Campo 2: Nome Completo (OBRIGATÓRIO)
    @Column(name = "nome_completo", nullable = false, length = 300)
    private String nomeCompleto;

    // Campo 3: Comunidade
    @Column(name = "comunidade", length = 200)
    private String comunidade;

    // Campo 4: Data de Baptismo (STRING, não LocalDate)
    @Column(name = "data_baptismo", length = 100)
    private String dataBaptismo;

    // Campo 5: Data de Nascimento (STRING)
    @Column(name = "data_nascimento", length = 100)
    private String dataNascimento;

    // Campo 6: Naturalidade
    @Column(name = "naturalidade", length = 200)
    private String naturalidade;

    // Campo 7: Nome do Pai
    @Column(name = "nome_pai", length = 300)
    private String nomePai;

    // Campo 8: Naturalidade do Pai
    @Column(name = "naturalidade_pai", length = 200)
    private String naturalidadePai;

    // Campo 9: Estado Civil
    @Column(name = "estado_civil", length = 50)
    private String estadoCivil;

    // Campo 10: Profissao
    @Column(name = "profissao", length = 200)
    private String profissao;

    // Campo 11: Nome da Mae
    @Column(name = "nome_mae", length = 300)
    private String nomeMae;

    // Campo 12: Avos Paternos
    @Column(name = "avos_paternos", length = 300)
    private String avosPaternos;

    // Campo 13: Avos Maternos
    @Column(name = "avos_maternos", length = 300)
    private String avosMaternos;

    // Campo 14: Nome do Padrinho
    @Column(name = "nome_padrinho", length = 300)
    private String nomePadrinho;

    // Campo 15: Estado Civil (padrinho) - header duplicado "Estado Civil.1"
    @Column(name = "estado_civil_padrinho", length = 50)
    private String estadoCivilPadrinho;

    // Campo 16: Profissao (padrinho) - "Profissao.1"
    @Column(name = "profissao_padrinho", length = 200)
    private String profissaoPadrinho;

    // Campo 17: Residencia (padrinho)
    @Column(name = "residencia_padrinho", length = 300)
    private String residenciaPadrinho;

    // Campo 18: Nome da Madrinha
    @Column(name = "nome_madrinha", length = 300)
    private String nomeMadrinha;

    // Campo 19: Estado Civil da Madrinha
    @Column(name = "estado_civil_madrinha", length = 50)
    private String estadoCivilMadrinha;

    // Campo 20: Profisssao da Madrinha (typo original mantido)
    @Column(name = "profissao_madrinha", length = 200)
    private String profissaoMadrinha;

    // Campo 21: Residencia da Madrinha
    @Column(name = "residencia_madrinha", length = 300)
    private String residenciaMadrinha;

    // Campo 22: Data do Crisma (STRING)
    @Column(name = "data_crisma", length = 100)
    private String dataCrisma;

    // Campo 23: Data do Casamento (STRING)
    @Column(name = "data_casamento", length = 100)
    private String dataCasamento;

    // Campo 24: Numero do Assento (OBRIGATORIAMENTE STRING, nunca numérico)
    @Column(name = "numero_assento", length = 100)
    private String numeroAssento;

    // Campo 25: Observacoes
    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    // Campos derivados (calculados automaticamente)
    @Column(name = "batizado", nullable = false)
    @Builder.Default
    private Boolean batizado = false;

    @Column(name = "crismado", nullable = false)
    @Builder.Default
    private Boolean crismado = false;

    @Column(name = "casado", nullable = false)
    @Builder.Default
    private Boolean casado = false;

    // Timestamps
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
